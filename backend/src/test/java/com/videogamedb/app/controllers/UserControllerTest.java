// package com.videogamedb.app.controllers;

// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.List;

// import org.junit.jupiter.api.AfterEach;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import static org.mockito.Mockito.doNothing;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.Pageable;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContext;
// import org.springframework.security.core.context.SecurityContextHolder;

// import com.videogamedb.app.dto.UserDTO;
// import com.videogamedb.app.models.User;
// import com.videogamedb.app.services.UserService;
// import com.videogamedb.app.util.EncryptionUtil;
// import com.videogamedb.app.util.UserMapper;

// @ExtendWith(MockitoExtension.class)
// class UserControllerTest {

//     @Mock
//     private UserService userService;

//     @Mock
//     private UserMapper userMapper;

//     @Mock
//     private EncryptionUtil encryptionUtil;

//     @InjectMocks
//     private UserController userController;

//     private User testUser;
//     private UserDTO testUserDTO;
//     private UserDTO decryptedUserDTO;

//     private final LocalDateTime FIXED_NOW = LocalDateTime.of(2025, 9, 23, 12, 0);

//     @BeforeEach
//     void setUp() {
//         // Setup test user entity
//         testUser = new User();
//         testUser.setId("1");
//         testUser.setUsernameEnc("encryptedUsername");
//         testUser.setEmailEnc("encryptedEmail");
//         testUser.setEmailMasked("u***@example.com");
//         testUser.setFirstNameEnc("encryptedFirstName");
//         testUser.setLastNameEnc("encryptedLastName");
//         testUser.setPasswordHash("passwordHash");
//         testUser.setPasswordSalt("passwordSalt");
//         testUser.setCreationDate(FIXED_NOW);
//         testUser.setRole("USER");
//         testUser.setPlatforms(Arrays.asList("Steam", "PS5"));

//         // Setup test DTO returned from service
//         testUserDTO = new UserDTO();
//         testUserDTO.setUsername("testuser");
//         testUserDTO.setEmail("test@example.com");
//         testUserDTO.setEmailMasked("t***@example.com");
//         testUserDTO.setFirstName("Test");
//         testUserDTO.setLastName("User");
//         testUserDTO.setRole("USER");
//         testUserDTO.setPlatforms(Arrays.asList("Steam", "PS5"));
//         testUserDTO.setCreationDate(FIXED_NOW);

//         // Setup decrypted DTO
//         decryptedUserDTO = new UserDTO();
//         decryptedUserDTO.setUsername("decryptedUser");
//         decryptedUserDTO.setEmail("decrypted@example.com");
//         decryptedUserDTO.setEmailMasked("d***@example.com");
//         decryptedUserDTO.setFirstName("Decrypted");
//         decryptedUserDTO.setLastName("User");
//         decryptedUserDTO.setRole("USER");
//         decryptedUserDTO.setPlatforms(Arrays.asList("Steam", "PS5"));
//         decryptedUserDTO.setCreationDate(FIXED_NOW);
//     }

//     // -------------------- Security Context Helpers --------------------
//     private void setupAdminSecurityContext() {
//         Authentication authentication = new UsernamePasswordAuthenticationToken(
//                 "admin",
//                 null,
//                 Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
//         SecurityContext securityContext = mock(SecurityContext.class);
//         when(securityContext.getAuthentication()).thenReturn(authentication);
//         SecurityContextHolder.setContext(securityContext);
//     }

//     private void setupUserSecurityContext() {
//         Authentication authentication = new UsernamePasswordAuthenticationToken(
//                 "user1",
//                 null,
//                 Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
//         SecurityContext securityContext = mock(SecurityContext.class);
//         when(securityContext.getAuthentication()).thenReturn(authentication);
//         SecurityContextHolder.setContext(securityContext);
//     }

//     // -------------------- Tests --------------------

//     @Test
//     void getAllUsers_AdminRole_ReturnsUsers() {
//         setupAdminSecurityContext();
//         Page<UserDTO> userPage = new PageImpl<>(List.of(testUserDTO));
//         when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);
//         when(encryptionUtil.decryptUserDTO(any(UserDTO.class))).thenReturn(decryptedUserDTO);

//         ResponseEntity<Page<UserDTO>> response = userController.getAllUsers(Pageable.unpaged());

//         assertNotNull(response);
//         assertEquals(200, response.getStatusCodeValue());
//         assertNotNull(response.getBody());
//         assertEquals(1, response.getBody().getTotalElements());
//         assertEquals("decryptedUser", response.getBody().getContent().get(0).getUsername());

//         verify(userService, times(1)).getAllUsers(any(Pageable.class));
//         verify(encryptionUtil, times(1)).decryptUserDTO(any(UserDTO.class));
//     }

//     @Test
//     void getUserById_AdminRole_ReturnsUser() {
//         setupAdminSecurityContext();
//         when(userService.getUserByIdReturnDTO("1")).thenReturn(testUserDTO);
//         when(encryptionUtil.decryptUserDTO(testUserDTO)).thenReturn(decryptedUserDTO);

//         ResponseEntity<UserDTO> response = userController.getUserById("1");

//         assertNotNull(response);
//         assertEquals(200, response.getStatusCodeValue());
//         assertEquals("decryptedUser", response.getBody().getUsername());

//         verify(userService, times(1)).getUserById("1");
//         verify(encryptionUtil, times(1)).decryptUserDTO(testUserDTO);
//     }

//     @Test
//     void getUserById_SameUser_ReturnsUser() {
//         setupUserSecurityContext();
//         when(userService.getUserByIdReturnDTO("1")).thenReturn(testUserDTO);
//         when(encryptionUtil.decryptUserDTO(testUserDTO)).thenReturn(decryptedUserDTO);

//         ResponseEntity<UserDTO> response = userController.getUserById("1");

//         assertNotNull(response);
//         assertEquals(200, response.getStatusCodeValue());

//         verify(userService, times(1)).getUserById("1");
//     }

//     @Test
//     void getUsersByRole_AdminRole_ReturnsUsers() {
//         setupAdminSecurityContext();
//         List<UserDTO> userList = List.of(testUserDTO, testUserDTO);
//         when(userService.getUsersByRole("USER")).thenReturn(userList);
//         when(encryptionUtil.decryptUserDTO(any(UserDTO.class))).thenReturn(decryptedUserDTO);

//         ResponseEntity<List<UserDTO>> response = userController.getUsersByRole("USER");

//         assertEquals(2, response.getBody().size());
//         assertEquals("decryptedUser", response.getBody().get(0).getUsername());
//         verify(userService, times(1)).getUsersByRole("USER");
//         verify(encryptionUtil, times(2)).decryptUserDTO(any(UserDTO.class));
//     }

//     @Test
//     void createUser_ValidUser_ReturnsCreatedUser() {
//         when(userService.createUser(any(User.class))).thenReturn(testUserDTO);

//         ResponseEntity<UserDTO> response = userController.createUser(testUser);

//         assertEquals(201, response.getStatusCodeValue());
//         assertEquals("testuser", response.getBody().getUsername());
//         verify(userService, times(1)).createUser(any(User.class));
//     }

//     @Test
//     void updateUser_AdminRole_ReturnsUpdatedUser() {
//         setupAdminSecurityContext();
//         when(userService.updateUser(eq("1"), any(User.class))).thenReturn(testUserDTO);

//         ResponseEntity<UserDTO> response = userController.updateUser("1", testUser);

//         assertEquals(200, response.getStatusCodeValue());
//         assertEquals("testuser", response.getBody().getUsername());
//         verify(userService, times(1)).updateUser(eq("1"), any(User.class));
//     }

//     @Test
//     void deleteUser_AdminRole_ReturnsNoContent() {
//         setupAdminSecurityContext();
//         doNothing().when(userService).deleteUser("1");

//         ResponseEntity<Void> response = userController.deleteUser("1");

//         assertEquals(204, response.getStatusCodeValue());
//         verify(userService, times(1)).deleteUser("1");
//     }

//     @Test
//     void getAllUsers_Pagination_ReturnsPaginatedResults() {
//         setupAdminSecurityContext();
//         Page<UserDTO> userPage = new PageImpl<>(List.of(testUserDTO, testUserDTO), Pageable.ofSize(10), 2);
//         when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);
//         when(encryptionUtil.decryptUserDTO(any(UserDTO.class))).thenReturn(decryptedUserDTO);

//         ResponseEntity<Page<UserDTO>> response = userController.getAllUsers(Pageable.ofSize(10));

//         assertEquals(2, response.getBody().getTotalElements());
//         assertEquals(10, response.getBody().getSize());
//         verify(encryptionUtil, times(2)).decryptUserDTO(any(UserDTO.class));
//     }

//     @AfterEach
//     void tearDown() {
//         SecurityContextHolder.clearContext();
//     }
// }
