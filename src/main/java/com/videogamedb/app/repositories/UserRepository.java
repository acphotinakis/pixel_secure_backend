package com.videogamedb.app.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.videogamedb.app.models.User;

public interface UserRepository extends MongoRepository<User, String> {

    @Query("{ 'username_enc': ?0 }")
    Optional<User> findByUsernameEnc(String usernameEnc);

    @Query("{ 'username_hash': ?0 }")
    Optional<User> findByUsernameHash(String usernameHash);

    @Query("{ 'email_enc': ?0 }")
    Optional<User> findByEmailEnc(String emailEnc);

    @Query("{ 'email_masked': ?0 }")
    Optional<User> findByEmailMasked(String emailMasked);

    List<User> findByRole(String role);

    Optional<User> findUserById(int id);

    @Query(value = "{ 'creationDate': { $gte: ?0 } }")
    List<User> findByCreationDateAfter(java.time.LocalDateTime date);

    @Query(value = "{ 'platforms': ?0 }")
    List<User> findByPlatform(String platform);

    @Query(value = "{ 'audit_token': ?0 }")
    Optional<User> findByAuditToken(String auditToken);

    @Query(value = "{ 'username_enc': ?0 }", exists = true)
    boolean existsByUsernameEnc(String usernameEnc);

    @Query(value = "{ 'email_enc': ?0 }", exists = true)
    boolean existsByEmailEnc(String emailEnc);

    // Find users by first name (case-insensitive, stored encrypted)
    List<User> findByFirstNameEncContainingIgnoreCase(String firstNameEnc);

    // Find users by last name (case-insensitive, stored encrypted)
    List<User> findByLastNameEncContainingIgnoreCase(String lastNameEnc);

    // Find users who own a specific game
    @Query("{ 'ownedGames.vgId': ?0 }")
    List<User> findUsersByOwnedGame(String vgId);

    // Find users who have played a specific game
    @Query("{ 'plays.vgId': ?0 }")
    List<User> findUsersByPlayedGame(String vgId);

    // Find users who have rated a specific game
    @Query("{ 'ratings.vgId': ?0 }")
    List<User> findUsersByRatedGame(String vgId);

    // Find users who follow a specific user
    @Query("{ 'follows.usernameEnc': ?0 }")
    List<User> findFollowersByUsernameEnc(String usernameEnc);

    // Find users who have accessed the system after a specific date
    @Query("{ 'accessDatetimes.time': { $gt: ?0 } }")
    List<User> findUsersByAccessAfter(Date date);

    // Find users with the most played time
    @Aggregation(pipeline = {
            "{ $unwind: '$plays' }",
            "{ $group: { _id: '$_id', totalPlayTime: { $sum: '$plays.timePlayed' } } }",
            "{ $sort: { totalPlayTime: -1 } }",
            "{ $limit: ?0 }"
    })
    List<User> findTopUsersByPlayTime(int limit);

    // Find users who own games on a specific platform
    @Query("{ 'platforms.platformName': ?0 }")
    List<User> findUsersByPlatform(String platformName);
}