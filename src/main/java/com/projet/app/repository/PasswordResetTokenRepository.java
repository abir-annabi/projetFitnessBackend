package com.projet.app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projet.app.models.DBUser;
import com.projet.app.models.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(DBUser user);
    List<PasswordResetToken> findByExpiryDateBefore(LocalDateTime date);
    
    // Optionnel: trouver tous les tokens d'un utilisateur
    List<PasswordResetToken> findByUser(DBUser user);
}