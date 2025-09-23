// package com.videogamedb.app.security;

// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import static org.mockito.ArgumentMatchers.any;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;

// import com.videogamedb.app.models.User;
// import com.videogamedb.app.repositories.UserRepository;
// import com.videogamedb.app.util.EncryptionUtil;

// @ExtendWith(MockitoExtension.class)
// class CustomUserDetailsServiceTest {

//     @Mock
//     private UserRepository userRepository;

//     @Mock
//     private EncryptionUtil encryptionUtil;

//     @InjectMocks
//     private CustomUserDetailsService customUserDetailsService;

//     private User testUser;

//     @BeforeEach
//     void setUp() {
//         testUser = new User();
//         testUser.setId("1");
//         testUser.setUsernameEnc("encryptedUsername");
//         testUser.setEmailEnc("encryptedEmail");
//         testUser.setPasswordHash("passwordHash");
//         testUser.setPasswordSalt("passwordSalt");
//         testUser.setRole("USER");
//     }

//     @Test
//     void loadUserByUsername_ValidUsername_ReturnsUserDetails() {
//         when(encryptionUtil.encryptField(any())).thenReturn("encryptedUsername");
//         when(userRepository.findByUsernameEnc(any())).thenReturn(Optional.of(testUser));

//         UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

//         assertNotNull(userDetails);
//         assertEquals("encryptedUsername", userDetails.getUsername());
//         assertEquals("passwordHash", userDetails.getPassword());
//         assertTrue(userDetails.getAuthorities().stream()
//                 .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

//         verify(userRepository, times(1)).findByUsernameEnc(any());
//         verify(encryptionUtil, times(1)).encryptField(any());
//     }

//     @Test
//     void loadUserByUsername_InvalidUsername_ThrowsException() {
//         when(encryptionUtil.encryptField(any())).thenReturn("encryptedUsername");
//         when(userRepository.findByUsernameEnc(any())).thenReturn(Optional.empty());

//         assertThrows(UsernameNotFoundException.class, () -> {
//             customUserDetailsService.loadUserByUsername("nonexistent");
//         });

//         verify(userRepository, times(1)).findByUsernameEnc(any());
//         verify(encryptionUtil, times(1)).encryptField(any());
//     }

//     @Test
//     void loadUserById_ValidId_ReturnsUserDetails() {
//         when(userRepository.findById("1")).thenReturn(Optional.of(testUser));

//         UserDetails userDetails = customUserDetailsService.loadUserById("1");

//         assertNotNull(userDetails);
//         assertEquals("encryptedUsername", userDetails.getUsername());
//         assertEquals("passwordHash", userDetails.getPassword());

//         verify(userRepository, times(1)).findById("1");
//     }

//     @Test
//     void loadUserById_InvalidId_ThrowsException() {
//         when(userRepository.findById("1")).thenReturn(Optional.empty());

//         assertThrows(UsernameNotFoundException.class, () -> {
//             customUserDetailsService.loadUserById("1");
//         });

//         verify(userRepository, times(1)).findById("1");
//     }
// }