package com.billpayments.billpaymentsystem.controller;

import com.billpayments.billpaymentsystem.payload.request.FundWalletRequest;
import com.billpayments.billpaymentsystem.payload.response.PaystackInitResponse;
import com.billpayments.billpaymentsystem.payload.response.WalletResponse;
import com.billpayments.billpaymentsystem.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
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
    public ResponseEntity<?> verifyPayment(@PathVariable String reference){
        walletService.verifyAndCreditWallet(reference);
        return ResponseEntity.ok("Wallet funded successfully");
    }
}
