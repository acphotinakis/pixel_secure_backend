// package com.videogamedb.app;

// import static
// org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static
// org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// import java.util.*;

// import com.videogamedb.app.controllers.AuthController;
// import com.videogamedb.app.controllers.UserController;
// import com.videogamedb.app.controllers.UserController.GamePlayRequest;
// import com.videogamedb.app.controllers.UserController.GameRequest;
// import com.videogamedb.app.controllers.UserController.RatingRequest;
// import com.videogamedb.app.models.User;
// import com.videogamedb.app.payload.LoginRequest;
// import com.videogamedb.app.payload.SignUpRequest;
// import com.videogamedb.app.repositories.UserRepository;
// import com.videogamedb.app.security.CustomUserDetailsService;
// import com.videogamedb.app.security.JwtTokenProvider;
// import com.videogamedb.app.services.UserService;
// import com.videogamedb.app.util.Pbkdf2Hasher;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.*;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
// import
// org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
// import
// org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.context.annotation.Import;
// import org.springframework.http.MediaType;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.test.web.servlet.MockMvc;
// import com.fasterxml.jackson.databind.ObjectMapper;

// @WebMvcTest({ AuthController.class, UserController.class })
// @ImportAutoConfiguration(exclude = SecurityAutoConfiguration.class)
// @AutoConfigureMockMvc(addFilters = false)
// public class ControllerTests {

// @Autowired
// private MockMvc mockMvc;

// @Autowired
// private ObjectMapper objectMapper;

// @MockBean
// private UserRepository userRepository;

// @MockBean
// private CustomUserDetailsService userDetailsService;

// @MockBean
// private Pbkdf2Hasher pbkdf2Hasher;

// @MockBean
// private JwtTokenProvider jwtProvider;

// @MockBean
// private UserService userService;

// private User testUser;

// @BeforeEach
// public void setup() {
// testUser = new User();
// testUser.setId("user_1");
// testUser.setUsernameEnc("encUser");
// testUser.setEmailEnc("encEmail@example.com");
// testUser.setEmailMasked("e***@example.com");
// testUser.setFirstNameEnc("FirstEnc");
// testUser.setLastNameEnc("LastEnc");
// testUser.setPasswordHash("hash123");
// testUser.setPasswordSalt("salt123");

// testUser.setRole("USER");
// testUser.setCreationDate(new Date());
// testUser.setAccessDatetimes(new ArrayList<>());
// testUser.setOwnedGames(new ArrayList<>());
// testUser.setPlays(new ArrayList<>());
// testUser.setRatings(new ArrayList<>());
// testUser.setPlatforms(new ArrayList<>());
// testUser.setFollows(new ArrayList<>());
// }

// // ---------------- AuthController Tests ----------------

// @Test
// public void testRegisterUser() throws Exception {
// SignUpRequest signUp = new SignUpRequest();
// signUp.setUsername("encUser");
// signUp.setEmail("encEmail@example.com");
// signUp.setFirstName("FirstEnc");
// signUp.setLastName("LastEnc");
// signUp.setPassword("password123");

// Mockito.when(userRepository.existsByUsernameEnc("encUser")).thenReturn(false);
// Mockito.when(userRepository.existsByEmailEnc("encEmail@example.com")).thenReturn(false);
// Mockito.when(pbkdf2Hasher.hashPasswordWithSalt("password123"))
// .thenReturn(new String[] { "hash123", "salt123" });
// Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(testUser);

// mockMvc.perform(post("/api/auth/register")
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(signUp)))
// .andExpect(status().isOk())
// .andExpect(jsonPath("$.message").value("User registered successfully"))
// .andExpect(jsonPath("$.username").value("encUser"));
// }

// @Test
// public void testLoginUserSuccess() throws Exception {
// LoginRequest login = new LoginRequest();
// login.setUsername("encUser");
// login.setPassword("password123");

// Mockito.when(userRepository.findByUsernameEnc("encUser")).thenReturn(Optional.of(testUser));
// Mockito.when(pbkdf2Hasher.verifyPassword("password123", "hash123",
// "salt123")).thenReturn(true);
// Mockito.when(jwtProvider.generateToken(Mockito.any())).thenReturn("mockJwtToken");

// mockMvc.perform(post("/api/auth/login")
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(login)))
// .andExpect(status().isOk())
// .andExpect(jsonPath("$.token").value("mockJwtToken"))
// .andExpect(jsonPath("$.username").value("encUser"));
// }

// @Test
// public void testLoginUserFail() throws Exception {
// LoginRequest login = new LoginRequest();
// login.setUsername("encUser");
// login.setPassword("wrongPassword");

// Mockito.when(userRepository.findByUsernameEnc("encUser")).thenReturn(Optional.of(testUser));
// Mockito.when(pbkdf2Hasher.verifyPassword("wrongPassword", "hash123",
// "salt123")).thenReturn(false);

// mockMvc.perform(post("/api/auth/login")
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(login)))
// .andExpect(status().is4xxClientError());
// }

// // ---------------- UserController Tests ----------------

// @Test
// public void testGetUserByUsername() throws Exception {
// Mockito.when(userRepository.findByUsernameEnc("encUser")).thenReturn(Optional.of(testUser));

// mockMvc.perform(get("/api/users/encUser"))
// .andExpect(status().isOk())
// .andExpect(jsonPath("$.usernameEnc").value("encUser"))
// .andExpect(jsonPath("$.role").value("USER"));
// }

// @Test
// public void testUpdateUser() throws Exception {
// User updatedUser = new User();
// updatedUser.setUsernameEnc("encUser");
// updatedUser.setEmailEnc("newEmail@example.com");

// Mockito.when(userService.updateUser(Mockito.eq("encUser"),
// Mockito.any(User.class)))
// .thenReturn(updatedUser);

// mockMvc.perform(put("/api/users/encUser")
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(updatedUser)))
// .andExpect(status().isOk())
// .andExpect(jsonPath("$.usernameEnc").value("encUser"))
// .andExpect(jsonPath("$.emailEnc").value("newEmail@example.com"));
// }

// @Test
// public void testDeleteUser() throws Exception {
// Mockito.doNothing().when(userService).deleteUser("encUser");

// mockMvc.perform(delete("/api/users/encUser"))
// .andExpect(status().isOk());
// }

// @Test
// public void testAddOwnedGame() throws Exception {
// GameRequest gameRequest = new GameRequest();
// gameRequest.setVgId("game_123");

// Mockito.doNothing().when(userService).addOwnedGame("encUser", "game_123");

// mockMvc.perform(post("/api/users/encUser/owned-games")
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(gameRequest)))
// .andExpect(status().isOk());
// }

// @Test
// public void testAddGamePlay() throws Exception {
// GamePlayRequest playRequest = new GamePlayRequest();
// playRequest.setVgId("game_123");
// playRequest.setTimePlayed(120);
// playRequest.setDatetimeOpened(new Date());

// Mockito.doNothing().when(userService)
// .addGamePlay("encUser", "game_123", 120, playRequest.getDatetimeOpened());

// mockMvc.perform(post("/api/users/encUser/gameplay")
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(playRequest)))
// .andExpect(status().isOk());
// }

// @Test
// public void testAddGameRating() throws Exception {
// RatingRequest ratingRequest = new RatingRequest();
// ratingRequest.setVgId("game_123");
// ratingRequest.setRating(5);

// Mockito.doNothing().when(userService).addGameRating("encUser", "game_123",
// 5);

// mockMvc.perform(post("/api/users/encUser/ratings")
// .contentType(MediaType.APPLICATION_JSON)
// .content(objectMapper.writeValueAsString(ratingRequest)))
// .andExpect(status().isOk());
// }

// @Test
// public void testFollowUser() throws Exception {
// Mockito.doNothing().when(userService).followUser("follower", "followed");

// mockMvc.perform(post("/api/users/follower/follow/followed"))
// .andExpect(status().isOk());
// }

// @Test
// public void testUnfollowUser() throws Exception {
// Mockito.doNothing().when(userService).unfollowUser("follower", "followed");

// mockMvc.perform(delete("/api/users/follower/follow/followed"))
// .andExpect(status().isOk());
// }

// @Test
// public void testGetFollowers() throws Exception {
// List<User> followers = Collections.singletonList(testUser);

// Mockito.when(userService.getFollowers("encUser")).thenReturn(followers);

// mockMvc.perform(get("/api/users/encUser/followers"))
// .andExpect(status().isOk())
// .andExpect(jsonPath("$[0].usernameEnc").value("encUser"));
// }

// @Test
// public void testGetTopUsersByPlayTime() throws Exception {
// List<User> topUsers = Collections.singletonList(testUser);

// Mockito.when(userService.getTopUsersByPlayTime(10)).thenReturn(topUsers);

// mockMvc.perform(get("/api/users/top-players?limit=10"))
// .andExpect(status().isOk())
// .andExpect(jsonPath("$[0].usernameEnc").value("encUser"));
// }

// }
