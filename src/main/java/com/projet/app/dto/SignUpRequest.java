//DTO qui contient les informations nécessaires pour inscrire un nouvel utilisateur (email, mot de passe, etc.).
package com.projet.app.dto;

import com.projet.app.models.Profile;

public class SignUpRequest {
    
    private String email;
    private String password;
    private String name;
    private Profile profile;
    private String phoneNumber;
    private Long structureId;
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Long getStructureId() {
        return structureId;
    }

    public void setStructureId(Long structureId) {
        this.structureId = structureId;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    
    // Getter for email
    public String getEmail() {
        return email;
    }

    // Setter for email
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }
    public Profile getProfile() {
    	return profile;
    }

    // Constructor with all arguments
    public SignUpRequest(String email, String password, String name, String role,String phoneNumber) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.profile= new Profile(role);
        this.phoneNumber=phoneNumber;
    }
}
