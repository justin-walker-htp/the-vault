package com.walker.the_vault.service;

import com.walker.the_vault.dto.AuthenticationResponse;
import com.walker.the_vault.dto.LoginRequest;
import com.walker.the_vault.model.User;
import com.walker.the_vault.repository.UserRepository;
import com.walker.the_vault.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(LoginRequest request) {
        // 1. Check if username/password match what is in the DB
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. If we get here, the user is valid. Get the user object.
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 3. Generate the token
        String jwtToken = jwtService.generateToken(user.getUsername());

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
