package com.billpayments.billpaymentsystem.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletResponse {
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal balance;
    private String message;
}
