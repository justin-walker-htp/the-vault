package com.walker.the_vault.repository;

import com.walker.the_vault.model.Credential;
import com.walker.the_vault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CredentialRepository extends JpaRepository<Credential, UUID> {
    // Custom query: Find all credentials belonging to a specific user
    List<Credential> findByUserId(UUID userId);

    Optional<Credential> findByIdAndUser(UUID id, User user);
}