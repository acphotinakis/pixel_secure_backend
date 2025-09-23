package com.videogamedb.app.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.videogamedb.app.models.User;
import com.videogamedb.app.util.EncryptionUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class UserPrincipal implements UserDetails {

    private String usernameEnc;

    @JsonIgnore
    private String email;

    @JsonIgnore
    private String passwordHash;

    @JsonIgnore
    private String passwordSalt;

    private Collection<? extends GrantedAuthority> authorities;

    private UserPrincipal(String usernameEnc, String email, String passwordHash,
            String passwordSalt, Collection<? extends GrantedAuthority> authorities) {
        this.usernameEnc = usernameEnc;
        this.email = email;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        return new UserPrincipal(
                user.getUsernameEnc(),
                user.getEmailEnc(),
                user.getPasswordHash(),
                user.getPasswordSalt(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return usernameEnc;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserPrincipal))
            return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(usernameEnc, that.usernameEnc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usernameEnc);
    }
}
