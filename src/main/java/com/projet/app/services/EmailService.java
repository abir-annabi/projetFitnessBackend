package com.projet.app.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.client.url}")
    private String clientUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetPasswordEmail(String toEmail, String token) {
        String resetLink = clientUrl.replace("/**", "") + "/reset-password?token=" + token;
        String subject = "Password Reset Request";
        String message = "Hello,\n\nClick the following link to reset your password:\n" + resetLink +
                "\n\nThis link will expire in 30 minutes.\n\nIf you didn't request a password reset, ignore this email.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(toEmail);
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }
}
