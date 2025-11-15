package com.projet.app.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projet.app.models.DBUser;
import com.projet.app.models.PasswordResetToken;
import com.projet.app.repository.DBUserRepository;
import com.projet.app.repository.PasswordResetTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class PasswordResetService {

    private final DBUserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(DBUserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }
    
    
   
    public void createPasswordResetToken(String email) {
        Optional<DBUser> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            // Pour la sécurité, ne pas révéler si l'email existe ou non
            return;
        }

        DBUser user = optionalUser.get();
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(30);

        // Option 1: Garder tous les tokens (plus flexible)
        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        tokenRepository.save(resetToken);

        // Option 2: Nettoyer les tokens expirés (recommandé)
        cleanExpiredTokens();
        
        emailService.sendResetPasswordEmail(user.getEmail(), token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken); // Nettoyer le token expiré
            throw new RuntimeException("Token expired");
        }

        DBUser user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Supprimer TOUS les tokens de cet utilisateur après reset réussi
        tokenRepository.deleteByUser(user);
    }

    @Transactional
    private void cleanExpiredTokens() {
        List<PasswordResetToken> expiredTokens = tokenRepository.findByExpiryDateBefore(LocalDateTime.now());
        tokenRepository.deleteAll(expiredTokens);
    }
}
