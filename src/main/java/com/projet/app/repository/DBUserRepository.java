package com.projet.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projet.app.models.DBUser;



@Repository
public interface DBUserRepository extends JpaRepository<DBUser, Long>{
	
	boolean existsByEmail(String email);//Vérifie si un utilisateur avec l'email donné existe dans la base de données.
	Optional<DBUser>  findByEmail(String email);//Recherche un utilisateur par son email.
	
	//Renvoie un Optional, ce qui permet de gérer les cas où aucun utilisateur n'est trouvé.
	
	
	
	
}
//L'interface agit comme un pont entre votre application et la base de données
//fournie par jpa