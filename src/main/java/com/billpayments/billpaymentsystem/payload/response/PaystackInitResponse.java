package com.billpayments.billpaymentsystem.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaystackInitResponse {
    private String authorizationUrl;
    private String reference;
    private String message;
}
