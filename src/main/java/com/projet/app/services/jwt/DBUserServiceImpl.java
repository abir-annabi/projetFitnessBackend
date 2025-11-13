package com.projet.app.services.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.projet.app.models.DBUser;
import com.projet.app.repository.DBUserRepository;

@Service
public class DBUserServiceImpl implements UserDetailsService {

    private final DBUserRepository dbUserRepository;

    @Autowired
    public DBUserServiceImpl(DBUserRepository dbUserRepository) {
        this.dbUserRepository = dbUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Utiliser findByEmail pour récupérer l'utilisateur
        DBUser dbUser = dbUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        // Retourner directement l'utilisateur DBUser
        return dbUser;
    }
    public DBUser findByEmail(String email) {
        return dbUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
    }


}
