package com.billpayments.billpaymentsystem.service;

import com.billpayments.billpaymentsystem.payload.request.FundWalletRequest;
import com.billpayments.billpaymentsystem.payload.response.PaystackInitResponse;
import com.billpayments.billpaymentsystem.payload.response.WalletResponse;

import java.security.Principal;

public interface WalletService {
    WalletResponse getBalance(Principal principal);
    PaystackInitResponse initiateFunding(FundWalletRequest request, Principal principal);
    void verifyAndCreditWallet(String reference);
}
