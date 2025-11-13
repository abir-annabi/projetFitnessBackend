package com.projet.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.projet.app.models.DBUser;
import com.projet.app.repository.DBUserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
@Service
public class VerificationServiceImpl implements VerificationService{

    @Autowired
    private DBUserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;
    @Override
    public void generateAndSendCode(String email) throws MessagingException {
        // Vérifiez si l'utilisateur existe
        DBUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Génération du code de vérification
        String code = generateVerificationCode();

        // **Mise à jour de l'utilisateur avec le code de vérification**
        user.setVerificationCode(code);
        userRepository.save(user);  // **Sauvegarde du code en base de données**

        // Envoi de l'e-mail avec le code
        sendEmail(email, code);
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 10000));
    }

    private void sendEmail(String email, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(email);
        helper.setSubject("Votre code de vérification");
        helper.setText("Votre code de vérification est : " + code, true);

        mailSender.send(message);
    }
    
    @Override
    public boolean verifyCode(String email, String code) {
        DBUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return user.getVerificationCode() != null && user.getVerificationCode().equals(code);
    }

}