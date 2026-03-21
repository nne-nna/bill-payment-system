package com.billpayments.billpaymentsystem.payload.response;

import com.billpayments.billpaymentsystem.enums.TransactionStatus;
import com.billpayments.billpaymentsystem.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private Long id;
    private TransactionType type;
    private TransactionStatus status;
    private BigDecimal amount;
    private String referenceId;
    private String billerReferenceId;
    private String description;
    private String details;
    private LocalDateTime createdAt;
}
