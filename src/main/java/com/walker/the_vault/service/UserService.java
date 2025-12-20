package com.walker.the_vault.service;

import com.walker.the_vault.model.User;
import com.walker.the_vault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service // 1. Tells Spring: "This class holds business logic."
@RequiredArgsConstructor // 2. Lombok: Auto-generates the constructor for dependency injection
public class UserService {

    private final UserRepository userRepository;

    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        return userRepository.save(user);
    }
}
