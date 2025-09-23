// backend/src/main/java/com/videogamedb/app/security/JwtTokenProvider.java
package com.videogamedb.app.security;

import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwtSecrets}") // Comma-separated list of secrets
    private String jwtSecretsConfig;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    @Value("${app.jwtRefreshExpirationInMs}")
    private long jwtRefreshExpirationInMs;

    private List<String> jwtSecrets;
    private AtomicReference<String> currentSecret;
    private int currentSecretIndex = 0;

    @PostConstruct
    public void init() {
        // Parse comma-separated secrets
        jwtSecrets = Arrays.asList(jwtSecretsConfig.split(","));
        if (jwtSecrets.isEmpty()) {
            throw new IllegalStateException("At least one JWT secret must be configured");
        }
        currentSecret = new AtomicReference<>(getEncodedSecret(jwtSecrets.get(0)));

        logger.info("JWT Token Provider initialized with {} secrets", jwtSecrets.size());
    }

    // Rotate secret every 24 hours
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void rotateSecret() {
        currentSecretIndex = (currentSecretIndex + 1) % jwtSecrets.size();
        currentSecret.set(getEncodedSecret(jwtSecrets.get(currentSecretIndex)));
        logger.info("JWT secret rotated to index: {}", currentSecretIndex);
    }

    private String getEncodedSecret(String secret) {
        return Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        return generateToken(authentication, jwtExpirationInMs);
    }

    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, jwtRefreshExpirationInMs);
    }

    private String generateToken(Authentication authentication, long expirationMs) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, currentSecret.get())
                .compact();
    }

    public boolean validateToken(String authToken) {
        // Try all secrets for validation (supports key rotation)
        for (int i = 0; i < jwtSecrets.size(); i++) {
            try {
                String secret = getEncodedSecret(jwtSecrets.get(i));
                Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
                return true;
            } catch (SignatureException ex) {
                // Try next secret
                continue;
            } catch (MalformedJwtException ex) {
                logger.error("Invalid JWT token");
                break;
            } catch (ExpiredJwtException ex) {
                logger.error("Expired JWT token");
                break;
            } catch (UnsupportedJwtException ex) {
                logger.error("Unsupported JWT token");
                break;
            } catch (IllegalArgumentException ex) {
                logger.error("JWT claims string is empty");
                break;
            }
        }
        return false;
    }

    public String getUsernameFromToken(String token) {
        // Try all secrets to extract username
        for (int i = 0; i < jwtSecrets.size(); i++) {
            try {
                String secret = getEncodedSecret(jwtSecrets.get(i));
                Claims claims = Jwts.parser()
                        .setSigningKey(secret)
                        .parseClaimsJws(token)
                        .getBody();
                return claims.getSubject();
            } catch (SignatureException ex) {
                // Try next secret
                continue;
            } catch (Exception ex) {
                break;
            }
        }
        throw new MalformedJwtException("Unable to extract username from token");
    }

    public Date getExpirationDateFromToken(String token) {
        for (int i = 0; i < jwtSecrets.size(); i++) {
            try {
                String secret = getEncodedSecret(jwtSecrets.get(i));
                Claims claims = Jwts.parser()
                        .setSigningKey(secret)
                        .parseClaimsJws(token)
                        .getBody();
                return claims.getExpiration();
            } catch (SignatureException ex) {
                continue;
            } catch (Exception ex) {
                break;
            }
        }
        throw new MalformedJwtException("Unable to extract expiration from token");
    }
}