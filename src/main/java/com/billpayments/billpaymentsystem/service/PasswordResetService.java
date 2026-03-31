package com.billpayments.billpaymentsystem.service;

import com.billpayments.billpaymentsystem.payload.request.ForgotPasswordRequest;
import com.billpayments.billpaymentsystem.payload.request.ResetPasswordRequest;

public interface PasswordResetService {
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
