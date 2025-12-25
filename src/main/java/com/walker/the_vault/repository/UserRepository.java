package com.walker.the_vault.repository;

import com.walker.the_vault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
// import java.util.UUID; // <--- REMOVED

@Repository
public interface UserRepository extends JpaRepository<User, Integer> { // <--- CHANGED UUID TO INTEGER

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    // REMOVED references to "email" because User.java no longer has an email field
}