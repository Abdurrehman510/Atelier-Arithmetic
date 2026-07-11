package com.mathquiz.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

/**
 * Provides AES-128 encryption/decryption with a machine-unique derived key
 * to secure user configuration and SQLite data files locally offline.
 */
public class CryptoHelper {

    private static SecretKeySpec secretKey;

    static {
        try {
            // Derive a stable, machine-unique key source from local OS credentials
            String keySource = System.getProperty("user.name") + 
                               System.getProperty("os.name") + 
                               System.getProperty("user.home");
            
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = sha.digest(keySource.getBytes(StandardCharsets.UTF_8));
            byte[] aesKey = Arrays.copyOf(keyBytes, 16); // 128-bit key
            secretKey = new SecretKeySpec(aesKey, "AES");
        } catch (Exception e) {
            System.err.println("Failed to initialize cryptographic key provider: " + e.getMessage());
        }
    }

    /** Encrypts string value to Base64 AES-128 ciphertext. */
    public static String encrypt(String value) {
        if (value == null || value.isEmpty()) return value;
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            System.err.println("Encryption failed: " + e.getMessage());
            return value;
        }
    }

    /** Decrypts Base64 ciphertext. Falls back to input in case of failure or legacy plaintext. */
    public static String decrypt(String value) {
        if (value == null || value.isEmpty()) return value;
        try {
            byte[] decoded = Base64.getDecoder().decode(value);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Graceful resilience: return raw input if decryption fails (e.g. legacy plain text config)
            return value;
        }
    }
}
