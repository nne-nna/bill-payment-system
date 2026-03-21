package com.billpayments.billpaymentsystem.service;

import com.billpayments.billpaymentsystem.enums.TransactionType;
import com.billpayments.billpaymentsystem.payload.response.TransactionResponse;

import java.security.Principal;
import java.util.List;

public interface TransactionService{
    List<TransactionResponse> getAllTransactions(Principal principal);
    List<TransactionResponse> getAllTransactionsByType(TransactionType transactionType, Principal principal);
    TransactionResponse getTransactionByReference(String referenceId, Principal principal);
}
