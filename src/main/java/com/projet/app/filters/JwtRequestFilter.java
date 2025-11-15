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
        "/signup", "/login", "/validateCaptcha", "/register", 
        "/password/forgot", "/password/reset", "/password/**"
    );

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
        
        // Vérifier si c'est une endpoint public
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                logger.error("Failed to extract username from JWT token: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = dbUserServiceImpl.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails)) {
                String role = jwtUtil.extractRole(token);
                GrantedAuthority authority = () -> role;

                UsernamePasswordAuthenticationToken authenticationToken = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, List.of(authority));

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Si ce n'est pas une endpoint public et qu'il n'y a pas de token valide
        if (!isPublicEndpoint(path) && (authHeader == null || !authHeader.startsWith("Bearer ") || 
            SecurityContextHolder.getContext().getAuthentication() == null)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(endpoint -> 
            path.startsWith(endpoint) || path.equals(endpoint));
    }
}
