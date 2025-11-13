package com.projet.app.services;

import jakarta.mail.MessagingException;

public interface VerificationService {
	public void generateAndSendCode(String email) throws MessagingException;
	public boolean verifyCode(String email, String code);

}
