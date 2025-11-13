package com.projet.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.projet.app.filters.JwtRequestFilter;
import com.projet.app.services.jwt.DBUserServiceImpl;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final DBUserServiceImpl dBUserServiceImpl;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, DBUserServiceImpl dBUserServiceImpl) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.dBUserServiceImpl = dBUserServiceImpl;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security.csrf(csrf -> csrf.disable())
                .authorizeRequests()
                .requestMatchers(HttpMethod.POST, "/api/types").permitAll()
                .requestMatchers("/signup", "/login", "/register", "/validateCaptcha").permitAll()
                .requestMatchers("/api/auth/**","/api/users/**", "/api/structures/**", "/api/types/**","/api/types", "/api/structures","/api/natures/**","/api/natures", "/api/signataires/**").permitAll()                
                .anyRequest().authenticated()
                .and()
                .csrf(csrf -> csrf.disable())
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(dBUserServiceImpl);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
