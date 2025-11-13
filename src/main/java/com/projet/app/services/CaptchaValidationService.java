package com.projet.app.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.projet.app.dto.CaptchaResponse;

@Service
public class CaptchaValidationService {

    private final String recaptchaSecretKey = "6LfeP74qAAAAABI-w4lm4ip_nRHdNYRhxgHYopPn";
    private final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean validateCaptcha(String captchaResponse) {
    	System.out.println("Token reçu pour le CAPTCHA : " + captchaResponse);
        RestTemplate restTemplate = new RestTemplate();

        // Préparer les en-têtes HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        // Préparer le corps de la requête avec les paramètres
        String requestBody = String.format("secret=%s&response=%s", recaptchaSecretKey, captchaResponse);

        // Créer l'objet HttpEntity
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // Envoyer la requête POST et récupérer la réponse
            ResponseEntity<CaptchaResponse> responseEntity = restTemplate.exchange(
                RECAPTCHA_VERIFY_URL,
                HttpMethod.POST,
                requestEntity,
                CaptchaResponse.class
            );

            CaptchaResponse response = responseEntity.getBody();

            // Vérifier si la réponse est valide
            return response != null && response.isSuccess();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
