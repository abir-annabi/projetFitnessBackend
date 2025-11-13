package com.projet.app.services.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.projet.app.repository.DBUserRepository;

@Service
public class CustomUserDetails implements UserDetailsService {

    @Autowired
    private DBUserRepository dbUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return dbUserRepository.findByEmail(email)
                               .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + email));
    }
}

