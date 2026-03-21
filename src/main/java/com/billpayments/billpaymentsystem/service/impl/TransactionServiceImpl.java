package com.billpayments.billpaymentsystem.service.impl;

import com.billpayments.billpaymentsystem.enums.TransactionType;
import com.billpayments.billpaymentsystem.exceptions.BadRequestException;
import com.billpayments.billpaymentsystem.exceptions.ResourceNotFoundException;
import com.billpayments.billpaymentsystem.models.Transaction;
import com.billpayments.billpaymentsystem.models.User;
import com.billpayments.billpaymentsystem.payload.response.TransactionResponse;
import com.billpayments.billpaymentsystem.repository.TransactionRepository;
import com.billpayments.billpaymentsystem.repository.UserRepository;
import com.billpayments.billpaymentsystem.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    //helper methods
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private TransactionResponse mapToResponse(Transaction transaction){
        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .amount(transaction.getAmount())
                .referenceId(transaction.getReferenceId())
                .billerReferenceId(transaction.getBillerReferenceId())
                .description(transaction.getDescription())
                .details(transaction.getDetails())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    @Override
    public List<TransactionResponse> getAllTransactions(Principal principal) {
        User user = getUser(principal.getName());
        log.info("Fetching all transactions for user: {}", user.getEmail());

        return transactionRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getAllTransactionsByType(TransactionType type, Principal principal) {
        User user = getUser(principal.getName());
        log.info("Fetching {} transaction for user: {}", type, user.getEmail());

        return transactionRepository
                .findByUserAndTypeOrderByCreatedAtDesc(user, type)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionResponse getTransactionByReference(String referenceId, Principal principal) {
        User user = getUser(principal.getName());
        log.info("Fetching transaction with reference: {}", referenceId);

        Transaction transaction = transactionRepository
                .findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with reference: " + referenceId
                ));

        if (!transaction.getUser().getId().equals(user.getId())){
            throw new BadRequestException("You are not authorized to view this transaction");
        }

        return mapToResponse(transaction);
    }
}
