package com.walker.the_vault.service;

import com.walker.the_vault.dto.AuthenticationResponse;
import com.walker.the_vault.dto.LoginRequest;
import com.walker.the_vault.model.User;
import com.walker.the_vault.repository.UserRepository;
import com.walker.the_vault.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Initializes the Mockito stunt doubles
class AuthenticationServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authService; // Injects the mocks into the real service

    @Test
    void authenticate_ShouldReturnToken_WhenCredentialsAreValid() {
        // GIVEN: Set up a fake scenario
        LoginRequest request = new LoginRequest("walker_agent_011", "securePass123");
        User mockUser = User.builder().username("walker_agent_011").build();

        // Tell the stunt doubles how to behave
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(anyString())).thenReturn("mocked-jwt-token");

        // WHEN: Call the actual service method
        var response = authService.authenticate(request);

        // THEN: Verify the results
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());

        // Ensure the authentication manager was actually asked to check the password
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void authenticate_ShouldThrowException_WhenPassswordIsIncorrect() {
        // --- GIVEN ---
        LoginRequest request = new LoginRequest("walker_agent_011", "WRONG_PASSWORD");

        // "Script" - the stunt double to throw an error
        // BadCredentialException is the standard Spring Security error for wrong passwords
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // --- WHEN & THEN
        // We assert that the code THROWS an exception instead of returning a response
        assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(request);
        });

        // Verify that the system STOPPED before ever reaching the token generator
        verify(jwtService, Mockito.never()).generateToken(anyString());
    }
}