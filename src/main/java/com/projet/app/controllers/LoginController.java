//gère la connexion des utilisateurs et renvoie un token JWT en cas de succès.
package com.projet.app.controllers;
import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projet.app.dto.LoginRequest;
import com.projet.app.dto.LoginResponse;
import com.projet.app.services.CaptchaValidationService;
import com.projet.app.services.jwt.DBUserServiceImpl;
import com.projet.app.utils.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:4200")
public class LoginController {
	
	private final AuthenticationManager authenticationManager;
	//AuthenticationManager : Utilisé pour authentifier l'utilisateur avec ses identifiants.
	private final DBUserServiceImpl dbUserServiceImpl;
	//DBUserServiceImpl : Service pour récupérer les informations de l'utilisateur depuis la base de données.
	private final JwtUtil jwtUtil;
	//jwtUtil est dédiée à la création, validation et extraction des informations des tokens JWT.
	private final CaptchaValidationService captchaValidationService;

	
	
	
	public LoginController(AuthenticationManager authenticationManager, DBUserServiceImpl dbUserServiceImpl,JwtUtil jwtUtil,CaptchaValidationService captchaValidationService) {
		this.authenticationManager = authenticationManager;
		this.dbUserServiceImpl = dbUserServiceImpl;
		this.jwtUtil = jwtUtil;
		this.captchaValidationService = captchaValidationService;
	}
	
	
	 	@PostMapping
	 	public LoginResponse login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws IOException {
	 		//vérifier si l'utilisateur existe et si les informations d'identification sont valides.
	 		
	 		
	 		if (!captchaValidationService.validateCaptcha(loginRequest.getCaptchaToken())) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid reCAPTCHA");
				return null;
			}
	 		
	 		
	 		//Si l'utilisateur est authentifié et peut obtenir un token JWT ou être autorisé à accéder aux ressources protégées.
	        try {
	            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
	            System.out.println("Captcha reçu : " + loginRequest.getCaptchaToken());
                
	        } catch (BadCredentialsException e) {
	            throw new BadCredentialsException("Incorrect email or password.");
	            
	            //BadCredentialsException si l'email ou le mot de passe est incorrect.
	            
	        } catch (DisabledException disabledException) {
	            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User is not activated");
	            return null;
	            //DisabledException si le compte de l'utilisateur est désactivé.
	        }
	        final UserDetails userDetails = dbUserServiceImpl.loadUserByUsername(loginRequest.getEmail());
	        //Appelle le service DBUserServiceImpl pour récupérer l'utilisateur par son email.
	        final String jwt = jwtUtil.generateToken(userDetails);
	        //Utilise JwtUtil pour générer un token JWT basé sur le nom d'utilisateur.

	        return new LoginResponse(jwt);
	        //Retourne un objet JSON contenant le token JWT généré.
	    }
}
