package com.billpayments.billpaymentsystem.controller;


import com.billpayments.billpaymentsystem.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import com.billpayments.billpaymentsystem.payload.response.TransactionResponse;
import com.billpayments.billpaymentsystem.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Validated
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(Principal principal){
        List<TransactionResponse> transactions = transactionService.getAllTransactions(principal);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByType(@RequestParam TransactionType type, Principal principal){
        List<TransactionResponse> transactions = transactionService.getAllTransactionsByType(type, principal);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{referenceId}")
    public ResponseEntity<TransactionResponse> getTransactionByReference(
            @PathVariable
            @NotBlank(message = "Reference ID is required")
            @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Reference ID format is invalid")
            String referenceId,
            Principal principal
    ){
        TransactionResponse transaction = transactionService.getTransactionByReference(referenceId, principal);
        return ResponseEntity.ok(transaction);
    }
}
