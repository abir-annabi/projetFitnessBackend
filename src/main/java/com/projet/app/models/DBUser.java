package com.projet.app.models;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "dbuser")
@Data
public class DBUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String password;
    private String email;
    
    @OneToOne(cascade = CascadeType.ALL)
    private Profile profile;
    private String verificationCode;
    private String phoneNumber;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "structure_id")
    @JsonIgnoreProperties("users") // Ignore la relation inverse



    // Getters and setters
    public String getPhoneNumber() {
        return phoneNumber;
    }
   

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
  

    // Implement UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + profile.getRole().toUpperCase())
        );
    }

    @Override
    public String getUsername() {
        return email; // Use email as the username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Update logic as needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Update logic as needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Update logic as needed
    }

    @Override
    public boolean isEnabled() {
        return true; // Update logic as needed
    }

    @Override
    public String toString() {
        return this.getProfile().getRole();
    }
    
    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    


}

