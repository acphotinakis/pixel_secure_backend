package com.videogamedb.app.services;

import com.videogamedb.app.audit.SensitiveDataAudit;
import com.videogamedb.app.dto.UserDTO;
import com.videogamedb.app.exceptions.ResourceNotFoundException;
import com.videogamedb.app.exceptions.UnauthorizedAccessException;
import com.videogamedb.app.models.User;
import com.videogamedb.app.payload.SignUpRequest;
import com.videogamedb.app.repositories.UserRepository;
import com.videogamedb.app.security.SecurityUtils;
import com.videogamedb.app.util.EncryptionUtil;
import com.videogamedb.app.util.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String USER_CACHE_PREFIX = "user:";
    private static final long CACHE_TTL = 30; // minutes

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    // === GET METHODS ===

    @SensitiveDataAudit(action = "READ_ALL_USERS", resource = "USER")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toDTO);
    }

    @SensitiveDataAudit(action = "READ_USER", resource = "USER")
    @Cacheable(value = "users", key = "#id")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public User getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return user;
    }

    @SensitiveDataAudit(action = "READ_USER", resource = "USER")
    @Cacheable(value = "users", key = "#id")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public UserDTO getUserByIdReturnDTO(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toDTO(user);
    }

    @SensitiveDataAudit(action = "READ_USERS_BY_ROLE", resource = "USER")
    @Cacheable(value = "usersByRole", key = "#role")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getUsersByRole(String role) {
        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @SensitiveDataAudit(action = "READ_USERS_CREATED_AFTER", resource = "USER")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getUsersCreatedAfter(LocalDateTime date) {
        List<User> users = userRepository.findByCreationDateAfter(date);
        return users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    // === CREATE/UPDATE/DELETE METHODS ===

    @SensitiveDataAudit(action = "CREATE_USER", resource = "USER", logParameters = true)
    @Transactional
    public UserDTO createUser(User user) {
        String currentUserRole = SecurityUtils.getCurrentUserRole();
        if ("ADMIN".equals(user.getRole()) && !"ADMIN".equals(currentUserRole)) {
            throw new UnauthorizedAccessException("Only admins can create admin users");
        }

        User savedUser = userRepository.save(user);
        cacheUser(savedUser);
        return userMapper.toDTO(savedUser);
    }

    @SensitiveDataAudit(action = "UPDATE_USER", resource = "USER", logParameters = true)
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "usersByRole", allEntries = true)
    })
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Transactional
    public UserDTO updateUser(String id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        String currentUserRole = SecurityUtils.getCurrentUserRole();
        if (!"ADMIN".equals(currentUserRole) && !user.getRole().equals(userDetails.getRole())) {
            throw new UnauthorizedAccessException("Only admins can change user roles");
        }

        user.setEmailMasked(userDetails.getEmailMasked());
        user.setRole(userDetails.getRole());
        user.setPlatforms(userDetails.getPlatforms());

        User updatedUser = userRepository.save(user);
        cacheUser(updatedUser);
        return userMapper.toDTO(updatedUser);
    }

    @SensitiveDataAudit(action = "DELETE_USER", resource = "USER")
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "usersByRole", allEntries = true)
    })
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Transactional
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
        evictUserFromCache(id);
    }

    // === AUTH-RELATED METHODS ===

    public User getUserByEncryptedUsername(String encryptedUsername) {
        return userRepository.findByUsernameEnc(encryptedUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username"));
    }

    @SensitiveDataAudit(action = "CREATE_USER", resource = "USER", logParameters = true)
    @Transactional
    public User registerNewUser(SignUpRequest signUpRequest, EncryptionUtil encryptionUtil) {
        // 1️⃣ Encrypt username/email for storage & check uniqueness
        String encryptedUsername = encryptionUtil.encryptField(signUpRequest.getUsername());
        if (userRepository.existsByUsernameEnc(encryptedUsername)) {
            throw new IllegalArgumentException("Username already taken");
        }

        String encryptedEmail = encryptionUtil.encryptField(signUpRequest.getEmail());
        if (userRepository.existsByEmailEnc(encryptedEmail)) {
            throw new IllegalArgumentException("Email already in use");
        }

        // 2️⃣ Hash the password using EncryptionUtil
        EncryptionUtil.HashedPassword hashedPassword = encryptionUtil.hashPassword(signUpRequest.getPassword());

        // 3️⃣ Map signup request → User entity
        User user = new User();
        user.setUsernameEnc(encryptedUsername);
        user.setUsernameHash(encryptionUtil.hashUsername(signUpRequest.getUsername())); // optional
        user.setEmailEnc(encryptedEmail);
        user.setEmailMasked(encryptionUtil.maskEmail(signUpRequest.getEmail()));
        user.setFirstNameEnc(encryptionUtil.encryptField(signUpRequest.getFirstName()));
        user.setLastNameEnc(encryptionUtil.encryptField(signUpRequest.getLastName()));
        user.setPasswordSalt(hashedPassword.getSalt());
        user.setPasswordHash(hashedPassword.getHash());
        user.setCreationDate(LocalDateTime.now());

        // 4️⃣ Set role
        String currentUserRole = SecurityUtils.getCurrentUserRole();
        if ("ADMIN".equals(signUpRequest.getRole()) && !"ADMIN".equals(currentUserRole)) {
            throw new UnauthorizedAccessException("Only admins can create admin users");
        }
        user.setRole(signUpRequest.getRole() != null ? signUpRequest.getRole() : "USER");

        // 5️⃣ Save and cache
        User savedUser = userRepository.save(user);
        cacheUser(savedUser);

        return savedUser;
    }

    // === CACHE MANAGEMENT METHODS ===

    private void cacheUser(User user) {
        String cacheKey = USER_CACHE_PREFIX + user.getId();
        redisTemplate.opsForValue().set(cacheKey, userMapper.toDTO(user), CACHE_TTL, TimeUnit.MINUTES);
    }

    private void evictUserFromCache(String userId) {
        String cacheKey = USER_CACHE_PREFIX + userId;
        redisTemplate.delete(cacheKey);
    }

    public UserDTO getCachedUser(String userId) {
        String cacheKey = USER_CACHE_PREFIX + userId;
        return (UserDTO) redisTemplate.opsForValue().get(cacheKey);
    }

    // === ADDITIONAL QUERY METHODS ===

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getUsersByPlatform(String platform) {
        List<User> users = userRepository.findByPlatform(platform);
        return users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public long getTotalUserCount() {
        return userRepository.count();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getUsersWithFailedLoginAttempts(int minAttempts) {
        // This would require adding a method to UserRepository
        // For example: List<User> findByFailedLoginAttemptsGreaterThan(int attempts);
        return userRepository.findAll().stream()
                .filter(user -> user.getFailedLoginAttempts() >= minAttempts)
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
}
// package com.videogamedb.app.services;

// import com.videogamedb.app.dto.UserDTO;
// import com.videogamedb.app.exceptions.ResourceNotFoundException;
// import com.videogamedb.app.exceptions.UnauthorizedAccessException;
// import com.videogamedb.app.models.User;
// import com.videogamedb.app.repositories.UserRepository;
// import com.videogamedb.app.security.SecurityUtils;
// import com.videogamedb.app.util.UserMapper;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.stream.Collectors;

// @Service
// public class UserService {

// private final UserRepository userRepository;
// private final UserMapper userMapper;

// public UserService(UserRepository userRepository, UserMapper userMapper) {
// this.userRepository = userRepository;
// this.userMapper = userMapper;
// }

// @PreAuthorize("hasRole('ADMIN')")
// public Page<UserDTO> getAllUsers(Pageable pageable) {
// Page<User> users = userRepository.findAll(pageable);
// return users.map(userMapper::toDTO);
// }

// @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
// public UserDTO getUserById(String id) {
// User user = userRepository.findById(id)
// .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " +
// id));
// return userMapper.toDTO(user);
// }

// @PreAuthorize("hasRole('ADMIN')")
// public List<UserDTO> getUsersByRole(String role) {
// List<User> users = userRepository.findByRole(role);
// return users.stream().map(userMapper::toDTO).collect(Collectors.toList());
// }

// @PreAuthorize("hasRole('ADMIN')")
// public List<UserDTO> getUsersCreatedAfter(LocalDateTime date) {
// List<User> users = userRepository.findByCreationDateAfter(date);
// return users.stream().map(userMapper::toDTO).collect(Collectors.toList());
// }

// @Transactional
// public UserDTO createUser(User user) {
// String currentUserRole = SecurityUtils.getCurrentUserRole();
// if ("ADMIN".equals(user.getRole()) && !"ADMIN".equals(currentUserRole)) {
// throw new UnauthorizedAccessException("Only admins can create admin users");
// }
// // All encryption handled in UserMapper/EncryptionUtil
// User savedUser = userRepository.save(user);
// return userMapper.toDTO(savedUser);
// }

// @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
// @Transactional
// public UserDTO updateUser(String id, User userDetails) {
// User user = userRepository.findById(id)
// .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " +
// id));

// String currentUserRole = SecurityUtils.getCurrentUserRole();
// if (!"ADMIN".equals(currentUserRole) &&
// !user.getRole().equals(userDetails.getRole())) {
// throw new UnauthorizedAccessException("Only admins can change user roles");
// }
// user.setEmailMasked(userDetails.getEmailMasked());
// user.setRole(userDetails.getRole());
// user.setPlatforms(userDetails.getPlatforms());

// User updatedUser = userRepository.save(user);
// return userMapper.toDTO(updatedUser);
// }

// @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
// @Transactional
// public void deleteUser(String id) {
// User user = userRepository.findById(id)
// .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " +
// id));
// userRepository.delete(user);
// }
// }
