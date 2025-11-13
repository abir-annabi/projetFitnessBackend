package com.projet.app.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.projet.app.services.jwt.DBUserServiceImpl;
import com.projet.app.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/signup", "/login", "/validateCaptcha", "/register"
    ); // Liste des routes publiques à exclure du filtrage

    private final DBUserServiceImpl dbUserServiceImpl;
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtRequestFilter(DBUserServiceImpl dbUserServiceImpl, JwtUtil jwtUtil) {
        this.dbUserServiceImpl = dbUserServiceImpl;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Vérifie si l'en-tête Authorization contient un token valide
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Extrait le token après "Bearer "
            try {
                username = jwtUtil.extractUsername(token); // Extrait le username du token
            } catch (Exception e) {
                // Log en cas d'échec d'extraction du username
                System.err.println("Échec lors de l'extraction du username du token JWT : " + e.getMessage());
            }
        }

        // Si un username a été extrait et qu'il n'est pas encore authentifié
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = dbUserServiceImpl.loadUserByUsername(username);

            // Valide le token
            if (jwtUtil.validateToken(token, userDetails)) {
                String role = jwtUtil.extractRole(token); // Récupère le rôle depuis le token
                GrantedAuthority authority = () -> role;

                // Crée un token d'authentification pour Spring Security
                UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, List.of(authority));

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }else if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Si le token est manquant ou mal formé, passer à la requête suivante sans authentifier
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header.");
            return; // Stopper l'exécution et retourner une réponse 401 Unauthorized
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Vérifie si une route fait partie des points d'accès publics
     * @param path Chemin de la requête
     * @return true si la route est publique, sinon false
     */
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.contains(path);
    }
}
