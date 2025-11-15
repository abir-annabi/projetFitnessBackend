package com.projet.app.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projet.app.services.PasswordResetService;

 @RestController
@RequestMapping("/password")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8097"})
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            passwordResetService.createPasswordResetToken(email);
            return ResponseEntity.ok("Password reset email sent successfully.");
        } catch (Exception e) {
            logger.error("Error sending password reset email: {}", e.getMessage());
            // ✅ Retourner une réponse appropriée au lieu de lancer une exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error sending email: " + e.getMessage());
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestParam String token,
                                          @RequestParam String newPassword) {
        try {
            passwordResetService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password reset successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error resetting password: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error resetting password");
        }
    }
}