package com.videogamedb.app.controllers;

import com.videogamedb.app.dto.UserDTO;
import com.videogamedb.app.models.User;
import com.videogamedb.app.services.UserService;
import com.videogamedb.app.util.EncryptionUtil;
import com.videogamedb.app.util.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final EncryptionUtil encryptionUtil;

    public UserController(UserService userService, UserMapper userMapper, EncryptionUtil encryptionUtil) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.encryptionUtil = encryptionUtil;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        Page<UserDTO> users = userService.getAllUsers(pageable)
                .map(encryptionUtil::decryptUserDTO);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        UserDTO user = encryptionUtil.decryptUserDTO(userService.getUserByIdReturnDTO(id));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String role) {
        List<UserDTO> users = userService.getUsersByRole(role).stream()
                .map(encryptionUtil::decryptUserDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/created-after")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsersCreatedAfter(@RequestParam LocalDateTime date) {
        List<UserDTO> users = userService.getUsersCreatedAfter(date).stream()
                .map(encryptionUtil::decryptUserDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Validated @RequestBody User user) {
        // All sensitive field encryption is handled in UserMapper/EncryptionUtil when
        // converting to entity
        UserDTO createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String id,
            @Validated @RequestBody User userDetails) {
        // All sensitive field encryption is handled in UserMapper/EncryptionUtil when
        // converting to entity
        UserDTO updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
