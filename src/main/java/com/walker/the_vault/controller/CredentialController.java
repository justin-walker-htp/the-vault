package com.walker.the_vault.controller;

import com.walker.the_vault.model.Credential;
import com.walker.the_vault.model.User;
import com.walker.the_vault.repository.CredentialRepository;
import com.walker.the_vault.service.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credentials")
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialRepository credentialRepository;
    private final EncryptionService encryptionService;

    // 1. SAVE a new secret
    @PostMapping
    public ResponseEntity<Credential> saveCredential(@AuthenticationPrincipal User user, @RequestBody Credential request) {
        // We take the plain text password from the request...
        String plainPassword = request.getEncryptedPassword();

        // ...scramble it using our new Service
        String scrambledPassword = encryptionService.encrypt(plainPassword);

        // ...and save the scrambled version to the DB.
        Credential credential = Credential.builder()
                .user(user) // Link it to the currently logged-in user
                .url(request.getUrl())
                .username((request.getUsername()))
                .encryptedPassword(scrambledPassword)
                .build();

        return ResponseEntity.ok(credentialRepository.save(credential));
    }

    // 2. GET all secrets (Decrypted)
    @GetMapping
    public ResponseEntity<List<Credential>> getMyCredentials(@AuthenticationPrincipal User user) {
        List<Credential> myCredentials = credentialRepository.findByUserId(user.getId());

        // Decrypt them before sending back to the user
        myCredentials.forEach(cred -> {
            String original = encryptionService.decrypt(cred.getEncryptedPassword());
            cred.setEncryptedPassword(original);
        });

        return ResponseEntity.ok(myCredentials);
    }
}
