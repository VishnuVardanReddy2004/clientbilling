package com.clientbilling.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:no-reply@clientbilling.local}")
    private String fromEmail;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /** Send the password reset link via SMTP (HTML email). */
    @Async
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, true, "UTF-8");

            h.setFrom(fromEmail);
            h.setTo(toEmail);
            h.setSubject("Reset Your ClientBilling Password");
            h.setText("""
                <p>Hello,</p>
                <p>We received a request to reset your password.</p>
                <p>
                  <a href="%s" style="background:#0d6efd;color:#fff;padding:10px 14px;
                     text-decoration:none;border-radius:6px;">
                     Click here to reset your password
                  </a>
                </p>
                <p>This link expires in 2 hours. If you didn’t request this, ignore this email.</p>
            """.formatted(resetLink), true);

            mailSender.send(msg);
            log.info("Reset email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send reset email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }
}
