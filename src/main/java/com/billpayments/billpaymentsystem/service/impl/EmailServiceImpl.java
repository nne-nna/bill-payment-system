package com.billpayments.billpaymentsystem.service.impl;

import com.billpayments.billpaymentsystem.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendPasswordResetEmail(String to, String resetLink, String firstName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Reset Your PayEase Password");
            helper.setText(buildEmailTemplate(firstName, resetLink), true);

            mailSender.send(message);
            log.info("Password reset email sent to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send reset email");
        }
    }

    private String buildEmailTemplate(String firstName, String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Arial', sans-serif; background: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 40px auto; background: white; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #16a34a, #15803d); padding: 40px 30px; text-align: center; }
                    .header h1 { color: white; margin: 0; font-size: 28px; }
                    .header p { color: #bbf7d0; margin: 8px 0 0; font-size: 14px; }
                    .body { padding: 40px 30px; }
                    .body p { color: #374151; font-size: 15px; line-height: 1.6; }
                    .button { display: block; width: fit-content; margin: 30px auto; background: #16a34a; color: white !important; text-decoration: none; padding: 14px 32px; border-radius: 10px; font-weight: bold; font-size: 15px; }
                    .warning { background: #fef9c3; border: 1px solid #fde047; border-radius: 8px; padding: 12px 16px; margin: 20px 0; }
                    .warning p { color: #854d0e; font-size: 13px; margin: 0; }
                    .footer { background: #f9fafb; padding: 20px 30px; text-align: center; border-top: 1px solid #e5e7eb; }
                    .footer p { color: #9ca3af; font-size: 12px; margin: 0; }
                    .link { word-break: break-all; color: #16a34a; font-size: 13px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>💳 PayEase</h1>
                        <p>Your trusted bill payment platform</p>
                    </div>
                    <div class="body">
                        <p>Hi <strong>%s</strong>,</p>
                        <p>We received a request to reset your PayEase password. Click the button below to create a new password:</p>
                        <a href="%s" class="button">Reset My Password</a>
                        <div class="warning">
                            <p>⚠️ This link will expire in <strong>30 minutes</strong>. If you did not request a password reset, please ignore this email — your password will remain unchanged.</p>
                        </div>
                        <p>If the button doesn't work, copy and paste this link into your browser:</p>
                        <p class="link">%s</p>
                    </div>
                    <div class="footer">
                        <p>© 2026 PayEase. All rights reserved.</p>
                        <p>This is an automated email, please do not reply.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(firstName, resetLink, resetLink);
    }
}