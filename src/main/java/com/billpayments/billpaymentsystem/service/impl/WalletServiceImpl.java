package com.billpayments.billpaymentsystem.service.impl;

import com.billpayments.billpaymentsystem.enums.NotificationType;
import com.billpayments.billpaymentsystem.enums.TransactionStatus;
import com.billpayments.billpaymentsystem.enums.TransactionType;
import com.billpayments.billpaymentsystem.exceptions.BadRequestException;
import com.billpayments.billpaymentsystem.exceptions.ResourceNotFoundException;
import com.billpayments.billpaymentsystem.models.Transaction;
import com.billpayments.billpaymentsystem.models.User;
import com.billpayments.billpaymentsystem.models.Wallet;
import com.billpayments.billpaymentsystem.payload.request.FundWalletRequest;
import com.billpayments.billpaymentsystem.payload.response.PaystackInitResponse;
import com.billpayments.billpaymentsystem.payload.response.WalletResponse;
import com.billpayments.billpaymentsystem.repository.TransactionRepository;
import com.billpayments.billpaymentsystem.repository.UserRepository;
import com.billpayments.billpaymentsystem.repository.WalletRepository;
import com.billpayments.billpaymentsystem.service.NotificationService;
import com.billpayments.billpaymentsystem.service.WalletService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import java.math.BigDecimal;
import kong.unirest.HttpResponse;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService{
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final Gson gson;

    @Value("${paystack.secret.key}")
    private String paystackSecretKey;

    @Value("${paystack.base.url}")
    private String paystackBaseUrl;

    private User getUser(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Wallet getWallet(User user){
        return walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
    }

    @Override
    //the principal is an object that represents the currently logged in user
    public WalletResponse getBalance(Principal principal){
        User user = getUser(principal.getName());
        Wallet wallet = getWallet(user);

        return WalletResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .balance(wallet.getBalance())
                .message("Wallet balance retrieved successfully")
                .build();
    }

    @Override
    public PaystackInitResponse initiateFunding(FundWalletRequest request, Principal principal){
        User user = getUser(principal.getName());
        log.info("Initiating wallet funding for: {}", user.getEmail());

        //unique reference for this transaction
        String reference = "FUND-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();

        //Building the request body for paystack
        Map<String, Object> body = new HashMap<>();
        body.put("email", user.getEmail());
        body.put("amount", request.getAmount().multiply(BigDecimal.valueOf(100)).intValue()); //paystack uses kobo
        body.put("reference", reference);

        //Call paystack initialization endpoint
        HttpResponse<String> response = Unirest.post(paystackBaseUrl + "/transaction/initialize")
                .header("Authorization", "Bearer " + paystackSecretKey) //digital signature
                .header("Content-Type", "application/json")
                .body(gson.toJson(body)) //converts java map into json for paystack
                .asString();

        log.info("Paystack init response: {}", response.getBody());

        JsonObject jsonResponse = gson.fromJson(response.getBody(), JsonObject.class); //convert paystack response into a java obj

        if(!jsonResponse.get("status").getAsBoolean()){
            throw new BadRequestException("Failed to initialize payment: " +
                    jsonResponse.get("message").getAsString());
        }

        JsonObject data = jsonResponse.getAsJsonObject("data");
        //drills into the data object inside the response and extracts the authorization url
        //this is paystack checkout page url and the user needs to visit to complete payment
        String authorizationUrl = data.get("authorization_url").getAsString();

        //save a pending transaction
        Transaction transaction = Transaction.builder()
                .user(user)
                .type(TransactionType.WALLET_FUNDING)
                .status(TransactionStatus.PENDING)
                .amount(request.getAmount())
                .referenceId(reference)
                .description("Wallet funding via paystack")
                .build();

        transactionRepository.save(transaction);
        log.info("Pending transaction saved with reference: {}", reference);

        return PaystackInitResponse.builder()
                .authorizationUrl(authorizationUrl)
                .reference(reference)
                .message("Payment initialized. Complete payment at the authorization URL.")
                .build();
    }

    @Override
    @Transactional
    public void verifyAndCreditWallet(String reference){
        log.info("Verifying payment with reference: {}", reference);

        //Call paystack verify endpoint. A get request to ask the payment status.
        HttpResponse<String> response = Unirest.get(paystackBaseUrl + "/transaction/verify/" + reference)
                .header("Authorization", "Bearer " + paystackSecretKey)
                .asString();

        log.info("Paystack verify response: {}", response.getBody());

        JsonObject jsonResponse = gson.fromJson(response.getBody(), JsonObject.class);

        if (!jsonResponse.get("status").getAsBoolean()) {
            throw new BadRequestException("Payment verification failed");
        }

        JsonObject data = jsonResponse.getAsJsonObject("data");
        String paymentStatus = data.get("status").getAsString();

        // Find the pending transaction
        Transaction transaction = transactionRepository.findByReferenceId(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (!"success".equals(paymentStatus)) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new BadRequestException("Payment was not successful");
        }

        // Check if already processed to avoid double crediting
        if (TransactionStatus.SUCCESS.equals(transaction.getStatus())) {
            throw new BadRequestException("Transaction already processed");
        }

        // Credit the wallet
        Wallet wallet = getWallet(transaction.getUser());
        wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
        walletRepository.save(wallet);

        // Update transaction status
        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        log.info("Wallet credited successfully for user: {}", transaction.getUser().getEmail());

        notificationService.createNotification(
                transaction.getUser(),
                NotificationType.WALLET_FUNDED,
                "Wallet Funded Successfully",
                "Your wallet has been credited with ₦" + transaction.getAmount() +
                        ". Your new balance is ₦" + wallet.getBalance(),
                transaction.getReferenceId()
        );
    }

    @Override
    @Transactional
    public void handlePaystackWebhook(String payload, String signature) {
        log.info("Received Paystack webhook");

        // Verify webhook signature
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(
                    paystackSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKey);
            byte[] hmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computedSignature = bytesToHex(hmac);

            if (!computedSignature.equals(signature)) {
                log.warn("Invalid webhook signature");
                return;
            }
        } catch (Exception e) {
            log.error("Webhook signature verification failed: {}", e.getMessage());
            return;
        }

        // Parse and process
        JsonObject event = gson.fromJson(payload, JsonObject.class);
        String eventType = event.get("event").getAsString();

        if ("charge.success".equals(eventType)) {
            JsonObject data = event.getAsJsonObject("data");
            String reference = data.get("reference").getAsString();
            log.info("Webhook charge.success for reference: {}", reference);
            verifyAndCreditWallet(reference);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
