// package com.videogamedb.app.util;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.test.util.ReflectionTestUtils;

// import static org.junit.jupiter.api.Assertions.*;

// @ExtendWith(MockitoExtension.class)
// class EncryptionUtilTest {

//     private EncryptionUtil encryptionUtil;

//     @BeforeEach
//     void setUp() {
//         encryptionUtil = new EncryptionUtil();
//         ReflectionTestUtils.setField(encryptionUtil, "encryptionKey", "V8ACU_XVui0O6_ZP6gs2smzJSFBMiVXhpmEaYf11tK8=");
//         encryptionUtil.init();
//     }

//     @Test
//     void encryptFieldAndDecryptField_ValidInput_ReturnsOriginalValue() {
//         String originalText = "test@example.com";

//         String encrypted = encryptionUtil.encryptField(originalText);
//         String decrypted = encryptionUtil.decryptField(encrypted);

//         assertNotNull(encrypted);
//         assertNotEquals(originalText, encrypted);
//         assertEquals(originalText, decrypted);
//     }

//     @Test
//     void encryptField_NullInput_ReturnsEmptyString() {
//         String result = encryptionUtil.encryptField(null);

//         assertEquals("", result);
//     }

//     @Test
//     void decryptField_NullInput_ReturnsEmptyString() {
//         String result = encryptionUtil.decryptField(null);

//         assertEquals("", result);
//     }

//     @Test
//     void hashPassword_ValidPassword_ReturnsHashAndSalt() {
//         String password = "securePassword123";

//         EncryptionUtil.HashedPassword result = encryptionUtil.hashPassword(password);

//         assertNotNull(result);
//         assertNotNull(result.getSalt());
//         assertNotNull(result.getHash());
//         assertTrue(result.getIterations() > 0);
//     }

//     @Test
//     void verifyPassword_CorrectPassword_ReturnsTrue() {
//         String password = "securePassword123";
//         EncryptionUtil.HashedPassword hashed = encryptionUtil.hashPassword(password);

//         boolean result = encryptionUtil.verifyPassword(password, hashed.getSalt(), hashed.getHash());

//         assertTrue(result);
//     }

//     @Test
//     void verifyPassword_IncorrectPassword_ReturnsFalse() {
//         String password = "securePassword123";
//         EncryptionUtil.HashedPassword hashed = encryptionUtil.hashPassword(password);

//         boolean result = encryptionUtil.verifyPassword("wrongPassword", hashed.getSalt(), hashed.getHash());

//         assertFalse(result);
//     }

//     @Test
//     void hashUsername_ValidUsername_ReturnsHash() {
//         String username = "testuser";

//         String hash = encryptionUtil.hashUsername(username);

//         assertNotNull(hash);
//         assertNotEquals(username, hash);
//     }

//     @Test
//     void hashUsername_SameUsername_ReturnsSameHash() {
//         String username = "testuser";

//         String hash1 = encryptionUtil.hashUsername(username);
//         String hash2 = encryptionUtil.hashUsername(username);

//         assertEquals(hash1, hash2);
//     }

//     @Test
//     void hashUsername_DifferentUsernames_ReturnsDifferentHashes() {
//         String username1 = "testuser1";
//         String username2 = "testuser2";

//         String hash1 = encryptionUtil.hashUsername(username1);
//         String hash2 = encryptionUtil.hashUsername(username2);

//         assertNotEquals(hash1, hash2);
//     }
// }