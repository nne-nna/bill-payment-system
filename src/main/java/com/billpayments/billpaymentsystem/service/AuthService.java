package com.billpayments.billpaymentsystem.service;

import com.billpayments.billpaymentsystem.payload.request.LoginRequest;
import com.billpayments.billpaymentsystem.payload.request.RegisterRequest;
import com.billpayments.billpaymentsystem.payload.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
