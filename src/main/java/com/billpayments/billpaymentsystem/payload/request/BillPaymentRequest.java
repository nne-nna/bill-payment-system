package com.billpayments.billpaymentsystem.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillPaymentRequest {
    @NotBlank(message = "Service ID is required")
    private String serviceID;

    @NotBlank(message = "Billers code is required")
    private String billersCode;

    @NotBlank(message = "Variation code is required")
    private String variationCode;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotBlank(message = "Phone number is required")
    private String phone;
}

/*
    ->serviceId e.g ikeja-electric, mtn, dstv. This determines what type of bill it is.
    ->billersCode e.g meter number, phone number, smart card number
    ->variationCode e.g prepaid/postpaid, data bundle, subscription package
*/
