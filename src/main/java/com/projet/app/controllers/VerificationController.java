package com.projet.app.controllers;	

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projet.app.dto.CodeVerificationRequest;
import com.projet.app.dto.EmailRequest;
import com.projet.app.services.VerificationService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/auth")
public class VerificationController {

    @Autowired
    private VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
    	this.verificationService= verificationService;
    }
    
    @PostMapping("/send-code")
    public ResponseEntity<String> sendVerificationCode(@RequestBody EmailRequest request) {
        try {
            String email = request.getEmail();
            verificationService.generateAndSendCode(email);
            return ResponseEntity.ok("Le code de vérification a été envoyé avec succès.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Erreur lors de l'envoi de l'email.");
        }
    }
    
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody CodeVerificationRequest request) {
        boolean isValid = verificationService.verifyCode(request.getEmail(), request.getCode());

        if (isValid) {
            return ResponseEntity.ok("Code de vérification valide !");
        } else {
            return ResponseEntity.badRequest().body("Code de vérification invalide.");
        }
    }

    
    

}
