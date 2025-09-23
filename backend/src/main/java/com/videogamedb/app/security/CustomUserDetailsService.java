package com.videogamedb.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.videogamedb.app.models.User;
import com.videogamedb.app.repositories.UserRepository;
import com.videogamedb.app.util.EncryptionUtil;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Encrypt the username for database lookup
        String encryptedUsername = encryptionUtil.encryptField(username);
        User user = userRepository.findByUsernameEnc(encryptedUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id: " + id));

        return UserPrincipal.create(user);
    }
}