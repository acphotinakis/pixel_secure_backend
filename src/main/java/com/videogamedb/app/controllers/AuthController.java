// backend/src/main/java/com/videogamedb/app/controllers/AuthController.java (Enhanced)
package com.videogamedb.app.controllers;

import com.videogamedb.app.audit.SensitiveDataAudit;
import com.videogamedb.app.models.RefreshToken;
import com.videogamedb.app.models.User;
import com.videogamedb.app.payload.*;
import com.videogamedb.app.repositories.RefreshTokenRepository;
import com.videogamedb.app.security.*;
import com.videogamedb.app.services.UserService;
import com.videogamedb.app.util.EncryptionUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserService userService;

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Autowired
    private JwtTokenProvider jwtProvider;

    @Autowired
    private RateLimitService rateLimitService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtBlacklistService jwtBlacklistService;

    @SensitiveDataAudit(action = "USER_LOGIN", resource = "AUTH", logParameters = true)
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // Rate limiting check
        String clientIp = getClientIpAddress();
        if (rateLimitService.isRateLimited(clientIp, RateLimitService.RateLimitType.LOGIN)) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Too many login attempts");
            response.put("retryAfter", "1 hour");
            return ResponseEntity.status(429).body(response);
        }

        try {
            // Encrypt the username for database lookup
            String encryptedUsername = encryptionUtil.encryptField(loginRequest.getUsername());
            User user = userService.getUserByEncryptedUsername(encryptedUsername);

            if (user.isAccountLocked()) {
                throw new BadCredentialsException("Account temporarily locked. Try again later.");
            }

            boolean passwordMatches = encryptionUtil.verifyPassword(
                    loginRequest.getPassword(),
                    user.getPasswordSalt(),
                    user.getPasswordHash());

            if (!passwordMatches) {
                user.incrementFailedAttempts();
                userService.updateUser(user.getId(), user);

                int remainingAttempts = 5 - user.getFailedLoginAttempts();
                throw new BadCredentialsException("Invalid credentials. " + remainingAttempts + " attempts remaining.");
            }

            // Successful login
            user.resetFailedAttempts();
            user.setLastLoginDate(LocalDateTime.now());
            userService.updateUser(user.getId(), user);

            rateLimitService.resetAttempts(clientIp, RateLimitService.RateLimitType.LOGIN);

            UserPrincipal userPrincipal = UserPrincipal.create(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal, null, userPrincipal.getAuthorities());

            String jwt = jwtProvider.generateToken(authentication);
            String refreshToken = generateRefreshToken(user.getId());
            String decryptedUsername = encryptionUtil.decryptField(user.getUsernameEnc());

            return ResponseEntity.ok(new JwtResponse(jwt, refreshToken, decryptedUsername,
                    user.getRole(), user.getLastLoginDate()));

        } catch (BadCredentialsException e) {
            rateLimitService.isRateLimited(clientIp, RateLimitService.RateLimitType.LOGIN);
            throw e;
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshTokenStr = refreshTokenRequest.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new BadCredentialsException("Refresh token expired");
        }

        if (refreshToken.isRevoked()) {
            throw new BadCredentialsException("Refresh token revoked");
        }

        User user = userService.getUserById(refreshToken.getUserId());
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities());

        String newJwt = jwtProvider.generateToken(authentication);
        String newRefreshToken = generateRefreshToken(user.getId());

        // Revoke old refresh token
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        String decryptedUsername = encryptionUtil.decryptField(user.getUsernameEnc());

        return ResponseEntity.ok(new JwtResponse(newJwt, newRefreshToken, decryptedUsername,
                user.getRole(), user.getLastLoginDate()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            jwtBlacklistService.blacklistToken(token);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    @SensitiveDataAudit(action = "USER_REGISTRATION", resource = "AUTH", logParameters = true)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        // Registration logic with auto-encryption via annotations
        User user = userService.registerNewUser(signUpRequest, encryptionUtil);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("username", signUpRequest.getUsername());
        response.put("userId", user.getId());

        return ResponseEntity.ok(response);
    }

    private String generateRefreshToken(String userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUserId(userId);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(30)); // 30 days expiry
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    private String getClientIpAddress() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

}