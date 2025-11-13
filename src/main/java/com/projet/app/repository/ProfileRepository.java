package com.projet.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projet.app.models.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
	Optional<Profile> findById(Long id);

}
