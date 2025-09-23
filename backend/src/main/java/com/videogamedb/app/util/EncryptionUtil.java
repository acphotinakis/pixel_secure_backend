package com.videogamedb.app.util;

import com.macasaet.fernet.Key;
import com.macasaet.fernet.Token;
import com.macasaet.fernet.Validator;
import com.videogamedb.app.dto.UserDTO;
import com.videogamedb.app.models.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Base64;
import java.util.function.Function;

@Component
public class EncryptionUtil {

    @Value("${ENCRYPTION_KEY}")
    private String encryptionKey;

    private Key fernetKey;
    private Validator<String> validator;

    private static final int DEFAULT_PBKDF2_ITERATIONS = 310000;
    private static final int PBKDF2_KEY_LENGTH = 256; // bits

    @PostConstruct
    public void init() {
        this.fernetKey = new Key(encryptionKey);
        TemporalAmount ttl = Duration.ofDays(365 * 100);
        this.validator = new Validator<String>() {
            @Override
            public TemporalAmount getTimeToLive() {
                return ttl;
            }

            @Override
            public Function<byte[], String> getTransformer() {
                return bytes -> new String(bytes, StandardCharsets.UTF_8);
            }
        };
    }

    // ---------- Fernet Encryption / Decryption ----------

    public String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) {
            return "***" + email.substring(atIndex);
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    public String encryptField(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            return "";
        }
        Token token = Token.generate(fernetKey, plaintext);
        return token.serialise();
    }

    public String decryptField(String tokenStr) {
        if (tokenStr == null || tokenStr.isEmpty()) {
            return "";
        }
        try {
            Token token = Token.fromString(tokenStr);
            return token.validateAndDecrypt(fernetKey, validator);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt field", e);
        }
    }

    // ---------- PBKDF2 Password Hashing / Verification ----------
    public HashedPassword hashPassword(String password) {
        return hashPassword(password, DEFAULT_PBKDF2_ITERATIONS);
    }

    public HashedPassword hashPassword(String password, int iterations) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, PBKDF2_KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();

            return new HashedPassword(
                    Base64.getEncoder().encodeToString(salt),
                    Base64.getEncoder().encodeToString(hash),
                    iterations);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean verifyPassword(String password, String saltB64, String hashB64) {
        try {
            byte[] salt = Base64.getDecoder().decode(saltB64);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, DEFAULT_PBKDF2_ITERATIONS,
                    PBKDF2_KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash).equals(hashB64);
        } catch (Exception e) {
            throw new RuntimeException("Error verifying password", e);
        }
    }

    // ---------- Username Hashing ----------
    public String hashUsername(String plaintext) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash username", e);
        }
    }

    // ---------- Helper class ----------
    public static class HashedPassword {
        private final String salt;
        private final String hash;
        private final int iterations;

        public HashedPassword(String salt, String hash, int iterations) {
            this.salt = salt;
            this.hash = hash;
            this.iterations = iterations;
        }

        public String getSalt() {
            return salt;
        }

        public String getHash() {
            return hash;
        }

        public int getIterations() {
            return iterations;
        }
    }

    public User encryptUser(User user) {
        if (user.getUsernameEnc() != null) {
            user.setUsernameEnc(encryptField(user.getUsernameEnc()));
        }
        if (user.getEmailEnc() != null) {
            user.setEmailEnc(encryptField(user.getEmailEnc()));
        }
        if (user.getFirstNameEnc() != null) {
            user.setFirstNameEnc(encryptField(user.getFirstNameEnc()));
        }
        if (user.getLastNameEnc() != null) {
            user.setLastNameEnc(encryptField(user.getLastNameEnc()));
        }
        if (user.getAuditToken() != null) {
            user.setAuditToken(encryptField(user.getAuditToken()));
        }
        // Hash username
        if (user.getUsernameEnc() != null) {
            user.setUsernameHash(hashUsername(user.getUsernameEnc()));
        }
        return user;
    }

    public UserDTO decryptUserDTO(UserDTO userDTO) {
        if (userDTO.getUsername() != null) {
            userDTO.setUsername(decryptField(userDTO.getUsername()));
        }
        if (userDTO.getEmail() != null) {
            userDTO.setEmail(decryptField(userDTO.getEmail()));
        }
        if (userDTO.getFirstName() != null) {
            userDTO.setFirstName(decryptField(userDTO.getFirstName()));
        }
        if (userDTO.getLastName() != null) {
            userDTO.setLastName(decryptField(userDTO.getLastName()));
        }
        return userDTO;
    }
}