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
public class BillPaymentResponse {
    private String referenceId;
    private String status;
    private String message;
    private BigDecimal amount;
    private BigDecimal walletBalanceAfter;
    private String token;
    private String purchasedCode;
    private String serviceID;
    private String billersCode;
    private String productName;
    private String units;
    private String tariff;
    private String customerName;
    private String customerAddress;
}

//token: electricity token
//purchased code: airtime/data PIN
