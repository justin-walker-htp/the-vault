package com.walker.the_vault.repository;

import com.walker.the_vault.model.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CredentialRepository extends JpaRepository<Credential, UUID> {
    // Custom query: Find all credentials belonging to a specific user
    List<Credential> findByUserId(UUID userId);
}