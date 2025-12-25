package com.walker.the_vault.repository;

import com.walker.the_vault.model.Credential;
import com.walker.the_vault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

// Changed UUID to Integer here as well v
public interface CredentialRepository extends JpaRepository<Credential, Integer> {

    // Finds all secrets belonging to a specific user
    List<Credential> findByUser(User user);

    // Finds a specific secret AND ensures the user owns it (Security check)
    Optional<Credential> findByIdAndUser(Integer id, User user);
}