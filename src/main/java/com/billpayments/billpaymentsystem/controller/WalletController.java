package com.billpayments.billpaymentsystem.controller;

import com.billpayments.billpaymentsystem.payload.request.FundWalletRequest;
import com.billpayments.billpaymentsystem.payload.response.PaystackInitResponse;
import com.billpayments.billpaymentsystem.payload.response.WalletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import com.billpayments.billpaymentsystem.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@Validated
@Slf4j
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    public ResponseEntity<WalletResponse> getBalance(Principal principal){
        WalletResponse response = walletService.getBalance(principal);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/fund")
    public ResponseEntity<PaystackInitResponse> fundwallet(@Valid @RequestBody FundWalletRequest request, Principal principal){
        PaystackInitResponse response = walletService.initiateFunding(request, principal);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify/{reference}")
    public ResponseEntity<?> verifyPayment(
            @PathVariable
            @NotBlank(message = "Reference is required")
            @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Reference format is invalid")
            String reference
    ){
        walletService.verifyAndCreditWallet(reference);
        return ResponseEntity.ok("Wallet funded successfully");
    }

    @PostMapping("/webhook/paystack")
    public ResponseEntity<?> paystackWebhook(
            @RequestBody String payload,
            @RequestHeader("x-paystack-signature") String signature) {
        walletService.handlePaystackWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }
}
