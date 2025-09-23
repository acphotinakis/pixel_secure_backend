// package com.videogamedb.app.controllers;

// import com.videogamedb.app.models.User;
// import com.videogamedb.app.payload.JwtResponse;
// import com.videogamedb.app.payload.LoginRequest;
// import com.videogamedb.app.payload.SignUpRequest;
// import com.videogamedb.app.repositories.UserRepository;
// import com.videogamedb.app.security.JwtTokenProvider;
// import com.videogamedb.app.util.EncryptionUtil;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.BadCredentialsException;

// import java.time.LocalDateTime;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class AuthControllerTest {

//     @Mock
//     private UserRepository userRepository;

//     @Mock
//     private EncryptionUtil encryptionUtil;

//     @Mock
//     private JwtTokenProvider jwtTokenProvider;

//     @InjectMocks
//     private AuthController authController;

//     private User testUser;
//     private LoginRequest loginRequest;
//     private SignUpRequest signUpRequest;

//     @BeforeEach
//     void setUp() {
//         testUser = new User();
//         testUser.setId("1");
//         testUser.setUsernameEnc("encryptedUsername");
//         testUser.setEmailEnc("encryptedEmail");
//         testUser.setEmailMasked("u***@example.com");
//         testUser.setPasswordHash("passwordHash");
//         testUser.setPasswordSalt("passwordSalt");
//         testUser.setCreationDate(LocalDateTime.now());
//         testUser.setRole("USER");

//         loginRequest = new LoginRequest();
//         loginRequest.setUsername("testuser");
//         loginRequest.setPassword("password123");

//         signUpRequest = new SignUpRequest();
//         signUpRequest.setUsername("testuser");
//         signUpRequest.setEmail("test@example.com");
//         signUpRequest.setPassword("password123");
//         signUpRequest.setFirstName("Test");
//         signUpRequest.setLastName("User");
//     }

//     @Test
//     void authenticateUser_ValidCredentials_ReturnsJwtResponse() {
//         when(userRepository.findByUsernameEnc(any())).thenReturn(Optional.of(testUser));
//         when(encryptionUtil.verifyPassword(any(), any(), any())).thenReturn(true);
//         when(jwtTokenProvider.generateToken(any())).thenReturn("jwtToken");
//         when(encryptionUtil.decryptField(any())).thenReturn("testuser");

//         ResponseEntity<?> response = authController.authenticateUser(loginRequest);

//         assertNotNull(response);
//         assertEquals(200, response.getStatusCodeValue());
//         assertTrue(response.getBody() instanceof JwtResponse);

//         JwtResponse jwtResponse = (JwtResponse) response.getBody();
//         assertEquals("jwtToken", jwtResponse.getToken());
//         assertEquals("testuser", jwtResponse.getUsername());

//         verify(userRepository, times(1)).findByUsernameEnc(any());
//         verify(encryptionUtil, times(1)).verifyPassword(any(), any(), any());
//     }

//     @Test
//     void authenticateUser_InvalidUsername_ThrowsException() {
//         when(userRepository.findByUsernameEnc(any())).thenReturn(Optional.empty());

//         assertThrows(BadCredentialsException.class, () -> {
//             authController.authenticateUser(loginRequest);
//         });

//         verify(userRepository, times(1)).findByUsernameEnc(any());
//         verify(encryptionUtil, never()).verifyPassword(any(), any(), any());
//     }

//     @Test
//     void authenticateUser_InvalidPassword_ThrowsException() {
//         when(userRepository.findByUsernameEnc(any())).thenReturn(Optional.of(testUser));
//         when(encryptionUtil.verifyPassword(any(), any(), any())).thenReturn(false);

//         assertThrows(BadCredentialsException.class, () -> {
//             authController.authenticateUser(loginRequest);
//         });

//         verify(userRepository, times(1)).findByUsernameEnc(any());
//         verify(encryptionUtil, times(1)).verifyPassword(any(), any(), any());
//     }

//     @Test
//     void registerUser_NewUser_ReturnsSuccessResponse() {
//         when(userRepository.existsByUsernameEnc(any())).thenReturn(false);
//         when(userRepository.existsByEmailEnc(any())).thenReturn(false);
//         when(encryptionUtil.hashPassword(any())).thenReturn(
//                 new EncryptionUtil.HashedPassword("salt", "hash", 310000));
//         when(userRepository.save(any())).thenReturn(testUser);

//         ResponseEntity<?> response = authController.registerUser(signUpRequest);

//         assertNotNull(response);
//         assertEquals(200, response.getStatusCodeValue());
//         assertTrue(response.getBody() instanceof java.util.Map);

//         verify(userRepository, times(1)).existsByUsernameEnc(any());
//         verify(userRepository, times(1)).existsByEmailEnc(any());
//         verify(userRepository, times(1)).save(any());
//     }

//     @Test
//     void registerUser_UsernameTaken_ReturnsErrorResponse() {
//         when(userRepository.existsByUsernameEnc(any())).thenReturn(true);

//         ResponseEntity<?> response = authController.registerUser(signUpRequest);

//         assertNotNull(response);
//         assertEquals(400, response.getStatusCodeValue());
//         assertTrue(response.getBody().toString().contains("Username is already taken"));

//         verify(userRepository, times(1)).existsByUsernameEnc(any());
//         verify(userRepository, never()).existsByEmailEnc(any());
//         verify(userRepository, never()).save(any());
//     }

//     @Test
//     void registerUser_EmailTaken_ReturnsErrorResponse() {
//         when(userRepository.existsByUsernameEnc(any())).thenReturn(false);
//         when(userRepository.existsByEmailEnc(any())).thenReturn(true);

//         ResponseEntity<?> response = authController.registerUser(signUpRequest);

//         assertNotNull(response);
//         assertEquals(400, response.getStatusCodeValue());
//         assertTrue(response.getBody().toString().contains("Email is already in use"));

//         verify(userRepository, times(1)).existsByUsernameEnc(any());
//         verify(userRepository, times(1)).existsByEmailEnc(any());
//         verify(userRepository, never()).save(any());
//     }
// }