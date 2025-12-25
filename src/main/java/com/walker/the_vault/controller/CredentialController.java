package com.walker.the_vault.controller;

import com.walker.the_vault.model.Credential;
import com.walker.the_vault.model.User;
import com.walker.the_vault.repository.CredentialRepository;
import com.walker.the_vault.service.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// import java.util.UUID; // <--- REMOVED (We use Integer now)

@RestController
@RequestMapping("/api/credentials")
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialRepository credentialRepository;
    private final EncryptionService encryptionService;

    // 1. SAVE a new secret
    @PostMapping
    public ResponseEntity<Credential> saveCredential(@AuthenticationPrincipal User user, @RequestBody Credential request) {
        String plainPassword = request.getEncryptedPassword();
        String scrambledPassword = encryptionService.encrypt(plainPassword);

        Credential credential = Credential.builder()
                .user(user)
                .url(request.getUrl())
                .username(request.getUsername())
                .encryptedPassword(scrambledPassword)
                .build();

        return ResponseEntity.ok(credentialRepository.save(credential));
    }

    // 2. GET all secrets (Decrypted)
    @GetMapping
    public ResponseEntity<List<Credential>> getMyCredentials(@AuthenticationPrincipal User user) {
        // Changed to findByUser (JPA handles the ID automatically)
        List<Credential> myCredentials = credentialRepository.findByUser(user);

        myCredentials.forEach(cred -> {
            String original = encryptionService.decrypt(cred.getEncryptedPassword());
            cred.setEncryptedPassword(original);
        });

        return ResponseEntity.ok(myCredentials);
    }

    // 3. UPDATE a secret
    @PutMapping("/{id}")
    public ResponseEntity<Credential> updateCredential(
            @PathVariable Integer id, // <--- CHANGED UUID TO INTEGER
            @RequestBody Credential request,
            @AuthenticationPrincipal User user
    ) {
        return credentialRepository.findByIdAndUser(id, user)
                .map(existingCredential -> {
                    existingCredential.setUrl(request.getUrl());
                    existingCredential.setUsername(request.getUsername());

                    String scrambledPassword = encryptionService.encrypt(request.getEncryptedPassword());
                    existingCredential.setEncryptedPassword(scrambledPassword);

                    return ResponseEntity.ok(credentialRepository.save(existingCredential));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. DELETE a secret
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCredential(
            @PathVariable Integer id, // <--- CHANGED UUID TO INTEGER
            @AuthenticationPrincipal User user
    ) {
        return credentialRepository.findByIdAndUser(id, user)
                .map(existingCredential -> {
                    credentialRepository.delete(existingCredential);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}