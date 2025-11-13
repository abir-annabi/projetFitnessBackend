package com.projet.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projet.app.dto.SignUpRequest;
import com.projet.app.models.DBUser;
import com.projet.app.repository.DBUserRepository;
import com.projet.app.services.AuthServiceImpl;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private AuthServiceImpl userService;
    private DBUserRepository dbUserRepository;

    

    // Get all users
    @GetMapping
    public List<DBUser> getAllUsers() {
        return userService.getAllUsers();
    }

    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<DBUser> getUserById(@PathVariable("id") Long id) {
        DBUser user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // Update a user
    @PutMapping("/{id}")
    public ResponseEntity<DBUser> updateUser(
            @PathVariable("id") Long id,
            @RequestBody SignUpRequest updatedUser) {
        
        DBUser user = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(user);
    }


    // Delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    // add a user
    @PostMapping
    public ResponseEntity<DBUser> createUser(@RequestBody SignUpRequest signUpRequest) {
        DBUser createdUser = userService.createUser(signUpRequest);
        if (createdUser != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        }
        return ResponseEntity.badRequest().build();
    }

    
    
    
}
