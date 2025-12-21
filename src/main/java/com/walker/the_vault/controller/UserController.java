package com.walker.the_vault.controller;

import com.walker.the_vault.model.User;
import com.walker.the_vault.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController // 1. Tells Spring this class handles HTTP requests
@RequestMapping("/api/users") // 2. All endpoints in this class start with /api/users
@RequiredArgsConstructor
public class UserController {

    private final UserService userService; // Inject the Service

    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User savedUser = userService.registerUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        // 1. Reach into the "Global Storage Box" to find who is logged in
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Case the "Principal" back to our User object
        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }
}
