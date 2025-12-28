package com.walker.the_vault.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {

    // This value comes from application.yaml (we will add it there later)
    // For the test, ReflectionTestUtils is manually injecting "12345678901234567890123456789012" into this field.
    @Value("${application.security.encryption-key}")
    private String secretKey;

    private static final String ALGORITHM = "AES";

    public String encrypt(String rawData) {
        try {
            // 1. Create the Key
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);

            // 2. Initialize the Lock (Cipher) in Encrypt Mode
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            // 3. Encrypt and wrap in Base64 (so it's safe to save as text)
            byte[] encryptedBytes = cipher.doFinal(rawData.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting data", e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            // 1. Create the key
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);

            // 2. Initialize the Lock (Cipher) in Decrypt Mode
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            // 3. Decode Base64 back to bytes, then Decrypt
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting data", e);
        }
    }

    @PostConstruct
    public void init() {
        System.out.println("============================================");
        System.out.println("🔍 SECURITY DEBUG REPORT");
        if (secretKey == null) {
            System.out.println("❌ Key is NULL");
        } else {
            System.out.println("✅ Key Loaded. Length: " + secretKey.length());
            System.out.println("🔑 First 3 chars: " + secretKey.substring(0, 3));
            System.out.println("🔑 Last 3 chars: " + secretKey.substring(secretKey.length() - 3));
        }
        System.out.println("============================================");
    }
}
