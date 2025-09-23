package com.videogamedb.app.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.videogamedb.app.annotation.EncryptedField;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Field("username_enc")
    @NotBlank
    @EncryptedField(searchable = true)
    private String usernameEnc;

    @Field("username_hash")
    @NotBlank
    private String usernameHash;

    @Field("email_enc")
    @NotBlank
    @EncryptedField
    private String emailEnc;

    @Field("email_masked")
    private String emailMasked;

    @Field("firstName_enc")
    @EncryptedField
    private String firstNameEnc;

    @Field("lastName_enc")
    @EncryptedField
    private String lastNameEnc;

    @Field("password_hash")
    @NotBlank
    private String passwordHash;

    @Field("password_salt")
    @NotBlank
    private String passwordSalt;

    @Field("creationDate")
    @NotNull
    private LocalDateTime creationDate;

    @NotBlank
    private String role;

    private List<String> platforms;

    @Field("audit_token")
    @EncryptedField
    private String auditToken;

    @Field("failed_login_attempts")
    private int failedLoginAttempts = 0;

    @Field("account_locked_until")
    private LocalDateTime accountLockedUntil;

    @Field("last_login_date")
    private LocalDateTime lastLoginDate;

    // Constructors
    public User() {
        this.creationDate = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsernameEnc() {
        return usernameEnc;
    }

    public void setUsernameEnc(String usernameEnc) {
        this.usernameEnc = usernameEnc;
    }

    public String getUsernameHash() {
        return usernameHash;
    }

    public void setUsernameHash(String usernameHash) {
        this.usernameHash = usernameHash;
    }

    public String getEmailEnc() {
        return emailEnc;
    }

    public void setEmailEnc(String emailEnc) {
        this.emailEnc = emailEnc;
    }

    public String getEmailMasked() {
        return emailMasked;
    }

    public void setEmailMasked(String emailMasked) {
        this.emailMasked = emailMasked;
    }

    public String getFirstNameEnc() {
        return firstNameEnc;
    }

    public void setFirstNameEnc(String firstNameEnc) {
        this.firstNameEnc = firstNameEnc;
    }

    public String getLastNameEnc() {
        return lastNameEnc;
    }

    public void setLastNameEnc(String lastNameEnc) {
        this.lastNameEnc = lastNameEnc;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
    }

    public String getAuditToken() {
        return auditToken;
    }

    public void setAuditToken(String auditToken) {
        this.auditToken = auditToken;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public LocalDateTime getAccountLockedUntil() {
        return accountLockedUntil;
    }

    public void setAccountLockedUntil(LocalDateTime accountLockedUntil) {
        this.accountLockedUntil = accountLockedUntil;
    }

    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public boolean isAccountLocked() {
        return accountLockedUntil != null && LocalDateTime.now().isBefore(accountLockedUntil);
    }

    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.accountLockedUntil = LocalDateTime.now().plusMinutes(30);
        }
    }

    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
        this.accountLockedUntil = null;
    }

}