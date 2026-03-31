package com.billpayments.billpaymentsystem.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String resetLink, String firstName);
}
