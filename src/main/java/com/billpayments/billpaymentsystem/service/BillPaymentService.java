package com.billpayments.billpaymentsystem.service;

import com.billpayments.billpaymentsystem.payload.request.BillPaymentRequest;
import com.billpayments.billpaymentsystem.payload.response.BillPaymentResponse;

import java.security.Principal;

public interface BillPaymentService {
    BillPaymentResponse payBill(BillPaymentRequest request, Principal principal);
}
