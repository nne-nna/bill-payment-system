package com.billpayments.billpaymentsystem.service.impl;

import com.billpayments.billpaymentsystem.exceptions.BadRequestException;
import com.billpayments.billpaymentsystem.exceptions.ResourceNotFoundException;
import com.billpayments.billpaymentsystem.models.PasswordResetToken;
import com.billpayments.billpaymentsystem.models.User;
import com.billpayments.billpaymentsystem.payload.request.ForgotPasswordRequest;
import com.billpayments.billpaymentsystem.payload.request.ResetPasswordRequest;
import com.billpayments.billpaymentsystem.repository.PasswordResetTokenRepository;
import com.billpayments.billpaymentsystem.repository.UserRepository;
import com.billpayments.billpaymentsystem.service.EmailService;
import com.billpayments.billpaymentsystem.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        log.info("Password reset requested for: {}", request.getEmail());

        // Find user — don't reveal if email exists or not (security)
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            // Return success anyway to prevent email enumeration
            log.info("Password reset requested for non-existent email: {}", request.getEmail());
            return;
        }

        User user = userOpt.get();

        // Delete any existing tokens for this user
        tokenRepository.deleteByUserId(user.getId());

        // Generate secure token
        String token = UUID.randomUUID().toString();

        // Save token with 30 min expiry
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        // Build reset link
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        // Send email
        emailService.sendPasswordResetEmail(
                user.getEmail(),
                resetLink,
                user.getFirstName()
        );

        log.info("Password reset email sent to: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        log.info("Processing password reset");

        // Check passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        // Find token
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired reset link"));

        // Check if already used
        if (resetToken.isUsed()) {
            throw new BadRequestException("This reset link has already been used");
        }

        // Check if expired
        if (resetToken.isExpired()) {
            throw new BadRequestException("This reset link has expired. Please request a new one");
        }

        // Update password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Password reset successfully for: {}", user.getEmail());
    }
}