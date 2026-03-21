package com.billpayments.billpaymentsystem.controller;

import com.billpayments.billpaymentsystem.payload.request.BillPaymentRequest;
import com.billpayments.billpaymentsystem.payload.response.BillPaymentResponse;
import com.billpayments.billpaymentsystem.service.BillPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
@Slf4j
public class BillPaymentController {

    private final BillPaymentService billPaymentService;

    @PostMapping("/pay")
    public ResponseEntity<BillPaymentResponse> payBill(
            @Valid @RequestBody BillPaymentRequest request,
            Principal principal) {
        log.info("Bill payment request received for service: {}", request.getServiceID());
        BillPaymentResponse response = billPaymentService.payBill(request, principal);
        return ResponseEntity.ok(response);
    }
}
