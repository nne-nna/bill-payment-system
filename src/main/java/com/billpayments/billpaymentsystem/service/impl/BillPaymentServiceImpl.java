package com.billpayments.billpaymentsystem.service.impl;

import com.billpayments.billpaymentsystem.enums.ServiceProvider;
import com.billpayments.billpaymentsystem.enums.TransactionStatus;
import com.billpayments.billpaymentsystem.enums.TransactionType;
import com.billpayments.billpaymentsystem.exceptions.BadRequestException;
import com.billpayments.billpaymentsystem.exceptions.ResourceNotFoundException;
import com.billpayments.billpaymentsystem.models.Transaction;
import com.billpayments.billpaymentsystem.models.User;
import com.billpayments.billpaymentsystem.models.Wallet;
import com.billpayments.billpaymentsystem.payload.request.BillPaymentRequest;
import com.billpayments.billpaymentsystem.payload.response.BillPaymentResponse;
import com.billpayments.billpaymentsystem.repository.TransactionRepository;
import com.billpayments.billpaymentsystem.repository.UserRepository;
import com.billpayments.billpaymentsystem.repository.WalletRepository;
import com.billpayments.billpaymentsystem.service.BillPaymentService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillPaymentServiceImpl implements BillPaymentService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final Gson gson;

    @Value("${vtpass.base.url}")
    private String vtpassBaseurl;

    @Value("${vtpass.api.key}")
    private String vtpassApiKey;

    @Value("${vtpass.secret.key}")
    private String vtpassSecretKey;

    @Value("${vtpass.public.key}")
    private String vtpassPublicKey;

    //Helper methods
    private String generateRequestId(TransactionType type){
        String prefix = switch(type){
            case ELECTRICITY -> "ELECT";
            case AIRTIME -> "AIR";
            case DATA -> "DATA";
            case CABLE_TV -> "CABLE";
            default -> "BILL";
        };
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return prefix + "-" + timestamp + "-" +
                (int)(Math.random() * 9000 + 1000);
    }

    private String buildDescription(BillPaymentRequest request){
        return request.getServiceID() + " payment for " + request.getBillersCode();
    }

    private String extractToken(JsonObject jsonResponse) {
        try {
            if (jsonResponse.has("purchased_code") &&
                    !jsonResponse.get("purchased_code").isJsonNull()) {
                String raw = jsonResponse.get("purchased_code").getAsString();
                return raw.replace("Token : ", "").trim();
            }
            if (jsonResponse.has("token") &&
                    !jsonResponse.get("token").isJsonNull()) {
                String raw = jsonResponse.get("token").getAsString();
                return raw.replace("Token : ", "").trim();
            }
        } catch (Exception e) {
            log.warn("Could not extract token from response");
        }
        return null;
    }

    private String extractProductName(JsonObject jsonResponse){
        try{
            return jsonResponse
                    .getAsJsonObject("content")
                    .getAsJsonObject("transactions")
                    .get("product_name")
                    .getAsString();
        } catch (Exception e){
            log.warn("Could not extract product name");
            return null;
        }
    }

    private String extractField(JsonObject jsonResponse, String fieldName) {
        try {
            if (jsonResponse.has(fieldName) &&
                    !jsonResponse.get(fieldName).isJsonNull()) {
                String value = jsonResponse.get(fieldName).getAsString();
                return "N/A".equals(value) ? null : value;
            }
        } catch (Exception e) {
            log.warn("Could not extract field: {}", fieldName);
        }
        return null;
    }

    @Override
    @Transactional
    public BillPaymentResponse payBill(BillPaymentRequest request, Principal principal) {
        //load user and wallet
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        //validate service provider
        ServiceProvider provider;
        try{
            provider = ServiceProvider.fromServiceId(request.getServiceID());
        } catch (IllegalArgumentException e){
            throw  new BadRequestException("Unsupported service: " + request.getServiceID());
        }

        TransactionType transactionType = provider.getTransactionType();
        log.info("Processing {} payment for user: {}", transactionType, user.getEmail());

        //check wallet balance
        if(wallet.getBalance().compareTo(request.getAmount()) < 0){
            throw new BadRequestException("Insufficient wallet balance. " +
                    "Current balance: ₦" + wallet.getBalance() +
                    ". Required: ₦" + request.getAmount());
        }

        //generate unique request id
        String requestId = generateRequestId(transactionType);

        //Deduct from wallet immediately
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);
        log.info("Deducted ₦{} from wallet. New balance: ₦{}",
                request.getAmount(), wallet.getBalance());

        //save pending transaction
        Transaction transaction = Transaction.builder()
                .user(user)
                .type(transactionType)
                .status(TransactionStatus.PENDING)
                .amount(request.getAmount())
                .referenceId(requestId)
                .description(buildDescription(request))
                .build();

        transactionRepository.save(transaction);

        //call vtpass
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("request_id", requestId);
            body.put("serviceID", request.getServiceID());
            body.put("billersCode", request.getBillersCode());
            body.put("variation_code", request.getVariationCode());
            body.put("amount", request.getAmount());
            body.put("phone", request.getPhone());

            log.info("Calling VTPass with body: {}", gson.toJson(body));

            HttpResponse<String> response = Unirest.post(vtpassBaseurl + "/pay")
                    .header("api-key", vtpassApiKey)
                    .header("public-key", vtpassPublicKey)
                    .header("secret-key", vtpassSecretKey)
                    .header("Content-Type", "application/json")
                    .body(gson.toJson(body))
                    .asString();

            String rawResponse = response.getBody();
            log.info("VTPass raw response: {}", rawResponse);
            log.info("VTPass HTTP status: {}", response.getStatus());

            // Safety check before parsing
            if (rawResponse == null || rawResponse.isBlank()) {
                throw new BadRequestException("VTPass returned an empty response");
            }

            // Check it looks like JSON before parsing
            if (!rawResponse.trim().startsWith("{")) {
                throw new BadRequestException("VTPass returned unexpected response: " + rawResponse);
            }

            //parse vtpass response
            JsonObject jsonResponse = gson.fromJson(rawResponse, JsonObject.class);
            String code = jsonResponse.has("code") ?
                    jsonResponse.get("code").getAsString() : "999";

            String responseDescription = null;
            if (jsonResponse.has("response_description") &&
                    !jsonResponse.get("response_description").isJsonNull()) {
                responseDescription = jsonResponse.get("response_description").getAsString();
            } else if (jsonResponse.has("message") &&
                    !jsonResponse.get("message").isJsonNull()) {
                responseDescription = jsonResponse.get("message").getAsString();
            } else {
                responseDescription = "Unknown error from VTPass";
            }

            log.info("VTPass code: {}, description: {}", code, responseDescription);

            //handle success or failure
            if ("000".equals(code)) {
                transaction.setStatus(TransactionStatus.SUCCESS);

                String token = extractToken(jsonResponse);
                String productName = extractProductName(jsonResponse);
                String units = extractField(jsonResponse, "units");
                String tariff = extractField(jsonResponse, "tariff");
                String customerName = extractField(jsonResponse, "customerName");
                String customerAddress = extractField(jsonResponse, "customerAddress");

                // Save details
                Map<String, Object> details = new HashMap<>();
                details.put("serviceID", request.getServiceID());
                details.put("billersCode", request.getBillersCode());
                details.put("variationCode", request.getVariationCode());
                details.put("token", token);
                details.put("units", units);
                details.put("tariff", tariff);
                details.put("productName", productName);
                details.put("customerName", customerName);
                details.put("customerAddress", customerAddress);
                transaction.setDetails(gson.toJson(details));
                transaction.setBillerReferenceId(requestId);
                transactionRepository.save(transaction);

                log.info("Bill payment successful for user: {}", user.getEmail());

                return BillPaymentResponse.builder()
                        .referenceId(requestId)
                        .status("SUCCESS")
                        .message("Bill payment successful")
                        .amount(request.getAmount())
                        .walletBalanceAfter(wallet.getBalance())
                        .token(token)
                        .purchasedCode(token)
                        .serviceID(request.getServiceID())
                        .billersCode(request.getBillersCode())
                        .productName(productName)
                        .units(units)
                        .tariff(tariff)
                        .customerName(customerName)
                        .customerAddress(customerAddress)
                        .build();
            } else {
                //failed(refund the wallet)
                log.error("VTPass payment failed. Code: {}, Description: {}",
                        code, responseDescription);

                wallet.setBalance(wallet.getBalance().add(request.getAmount()));
                walletRepository.save(wallet);

                transaction.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(transaction);

                throw new BadRequestException("Bill payment failed: " + responseDescription);
            }
        } catch (BadRequestException e){
            throw e;
        } catch (Exception e){
            //sth unexpected happened. refund wallet
            log.error("Unexpected error during bill payment: {}", e.getMessage(), e);

            wallet.setBalance(wallet.getBalance().add(request.getAmount()));
            walletRepository.save(wallet);

            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);

            throw new BadRequestException("Bill payment failed: " + e.getMessage());
        }
    }
}
