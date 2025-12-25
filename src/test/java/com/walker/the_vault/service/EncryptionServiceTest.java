package com.walker.the_vault.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionServiceTest {

    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionService();
        // We will inject a fake 32-character key for testing
        // We will implement the field 'secretKey' in the real class later
        ReflectionTestUtils.setField(encryptionService, "secretKey", "12345678901234567890123456789012");
    }

    @Test
    void shouldEncryptAndDecryptSuccessfully() {
        // GIVEN
        String originalPassword = "mySuperSecretPassword123!";

        // WHEN
        String encrypted = encryptionService.encrypt(originalPassword);
        String decrypted = encryptionService.decrypt(encrypted);

        // THEN
        assertNotNull(encrypted, "Encryption should not return null");
        assertNotEquals(originalPassword, encrypted, "Encrypted data should look different from raw data");
        assertEquals(originalPassword, decrypted, "Decryption must restore the original value");
    }

}