//nterface définissant la méthode pour créer un utilisateur.
package com.projet.app.services;

import java.util.List;

import com.projet.app.dto.SignUpRequest;
import com.projet.app.models.DBUser;

public interface AuthService {
	DBUser createUser(SignUpRequest signupRequest);

	List<DBUser> getAllUsers();

    DBUser getUserById(Long id);

    public DBUser updateUser(Long id, SignUpRequest updatedUser);

    void deleteUser(Long id);

}
	