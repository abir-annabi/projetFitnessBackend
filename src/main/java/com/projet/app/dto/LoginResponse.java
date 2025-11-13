//DTO contenant le token JWT retourné après une authentification réussie.
package com.projet.app.dto;

public record LoginResponse(String jwt) {
	// représente la réponse que le backend renvoie au client (frontend) après une authentification réussie.
}
