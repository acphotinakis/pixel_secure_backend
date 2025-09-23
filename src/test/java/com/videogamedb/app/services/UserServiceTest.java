// package com.videogamedb.app.services;

// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import static org.mockito.ArgumentMatchers.any;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockedStatic;
// import static org.mockito.Mockito.doNothing;
// import static org.mockito.Mockito.mockStatic;
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.Pageable;

// import com.videogamedb.app.dto.UserDTO;
// import com.videogamedb.app.exceptions.ResourceNotFoundException;
// import com.videogamedb.app.exceptions.UnauthorizedAccessException;
// import com.videogamedb.app.models.User;
// import com.videogamedb.app.repositories.UserRepository;
// import com.videogamedb.app.security.SecurityUtils;
// import com.videogamedb.app.util.UserMapper;

// @ExtendWith(MockitoExtension.class)
// class UserServiceTest {

//     @Mock
//     private UserRepository userRepository;

//     @Mock
//     private UserMapper userMapper;

//     @InjectMocks
//     private UserService userService;

//     private User testUser;
//     private UserDTO testUserDTO;

//     @BeforeEach
//     void setUp() {
//         testUser = new User();
//         testUser.setId("1");
//         testUser.setUsernameEnc("encryptedUsername");
//         testUser.setEmailEnc("encryptedEmail");
//         testUser.setEmailMasked("u***@example.com");
//         testUser.setFirstNameEnc("encryptedFirstName");
//         testUser.setLastNameEnc("encryptedLastName");
//         testUser.setPasswordHash("passwordHash");
//         testUser.setPasswordSalt("passwordSalt");
//         testUser.setCreationDate(LocalDateTime.now());
//         testUser.setRole("USER");
//         testUser.setPlatforms(Arrays.asList("Steam", "PS5"));

//         testUserDTO = new UserDTO();
//         testUserDTO.setUsername("testuser");
//         testUserDTO.setEmail("test@example.com");
//         testUserDTO.setEmailMasked("t***@example.com");
//         testUserDTO.setFirstName("Test");
//         testUserDTO.setLastName("User");
//         testUserDTO.setRole("USER");
//         testUserDTO.setPlatforms(Arrays.asList("Steam", "PS5"));
//     }

//     @Test
//     void getAllUsers_AdminRole_ReturnsUsers() {
//         try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
//             securityUtils.when(SecurityUtils::getCurrentUserRole).thenReturn("ADMIN");

//             Page<User> userPage = new PageImpl<>(List.of(testUser));
//             when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
//             when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

//             Page<UserDTO> result = userService.getAllUsers(Pageable.unpaged());

//             assertNotNull(result);
//             assertEquals(1, result.getTotalElements());
//             verify(userRepository, times(1)).findAll(any(Pageable.class));
//         }
//     }

//     @Test
//     void getAllUsers_NonAdminRole_ThrowsException() {
//         try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
//             securityUtils.when(SecurityUtils::getCurrentUserRole).thenReturn("USER");

//             assertThrows(UnauthorizedAccessException.class, () -> {
//                 userService.getAllUsers(Pageable.unpaged());
//             });
//         }
//     }

//     @Test
//     void getUserById_AdminRole_ReturnsUser() {
//         try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
//             securityUtils.when(SecurityUtils::getCurrentUserRole).thenReturn("ADMIN");

//             when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
//             when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

//             UserDTO result = userService.getUserByIdReturnDTO("1");

//             assertNotNull(result);
//             assertEquals("testuser", result.getUsername());
//             verify(userRepository, times(1)).findById("1");
//         }
//     }

//     @Test
//     void getUserById_NonAdminButOwnId_ReturnsUser() {
//         try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
//             securityUtils.when(SecurityUtils::getCurrentUserRole).thenReturn("USER");

//             when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
//             when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

//             UserDTO result = userService.getUserByIdReturnDTO("1");

//             assertNotNull(result);
//             assertEquals("testuser", result.getUsername());
//             verify(userRepository, times(1)).findById("1");
//         }
//     }

//     @Test
//     void getUserById_NonAdminDifferentId_ThrowsException() {
//         try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
//             securityUtils.when(SecurityUtils::getCurrentUserRole).thenReturn("USER");

//             assertThrows(UnauthorizedAccessException.class, () -> {
//                 userService.getUserById("1");
//             });
//         }
//     }

//     @Test
//     void getUserById_UserNotFound_ThrowsException() {
//         try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
//             securityUtils.when(SecurityUtils::getCurrentUserRole).thenReturn("ADMIN");

//             when(userRepository.findById("1")).thenReturn(Optional.empty());

//             assertThrows(ResourceNotFoundException.class, () -> {
//                 userService.getUserById("1");
//             });
//         }
//     }

//     @Test
//     void createUser_AdminCreatesAdmin_Success() {
//         try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
//             securityUtils.when(SecurityUtils::getCurrentUserRole).thenReturn("ADMIN");

//             testUser.setRole("ADMIN");
//             when(userRepository.save(any(User.class))).thenReturn(testUser);
//             when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

//             UserDTO result = userService.createUser(testUser);

//             assertNotNull(result);
//             assertEquals("testuser", result.getUsername());
//             verify(userRepository, times(1)).save(any(User.class));
//         }
//     }

//     @Test
//     void createUser_NonAdminCreatesAdmin_ThrowsException() {
//         try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
//             securityUtils.when(SecurityUtils::getCurrentUserRole).thenReturn("USER");

//             testUser.setRole("ADMIN");

//             assertThrows(UnauthorizedAccessException.class, () -> {
//                 userService.createUser(testUser);
//             });
//         }
//     }

//     @Test
//     void updateUser_AdminUpdatesUser_Success() {
//         try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
//             securityUtils.when(SecurityUtils::getCurrentUserRole).thenReturn("ADMIN");

//             when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
//             when(userRepository.save(any(User.class))).thenReturn(testUser);
//             when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

//             UserDTO result = userService.updateUser("1", testUser);

//             assertNotNull(result);
//             assertEquals("testuser", result.getUsername());
//             verify(userRepository, times(1)).findById("1");
//             verify(userRepository, times(1)).save(any(User.class));
//         }
//     }

//     @Test
//     void updateUser_NonAdminChangesRole_ThrowsException() {
//         try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
//             securityUtils.when(SecurityUtils::getCurrentUserRole).thenReturn("USER");

//             User updatedUser = new User();
//             updatedUser.setRole("ADMIN");

//             when(userRepository.findById("1")).thenReturn(Optional.of(testUser));

//             assertThrows(UnauthorizedAccessException.class, () -> {
//                 userService.updateUser("1", updatedUser);
//             });
//         }
//     }

//     @Test
//     void deleteUser_AdminDeletesUser_Success() {
//         try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
//             securityUtils.when(SecurityUtils::getCurrentUserRole).thenReturn("ADMIN");

//             when(userRepository.findById("1")).thenReturn(Optional.of(testUser));
//             doNothing().when(userRepository).delete(any(User.class));

//             userService.deleteUser("1");

//             verify(userRepository, times(1)).findById("1");
//             verify(userRepository, times(1)).delete(any(User.class));
//         }
//     }
// }