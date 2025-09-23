package com.videogamedb.app.repositories;

import com.videogamedb.app.models.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);

    @Query("{ 'user_id': ?0, 'revoked': false }")
    List<RefreshToken> findByUserId(String userId);

    @Query("{ 'expiry_date': { $lt: ?0 } }")
    List<RefreshToken> findByExpiryDateBefore(java.time.LocalDateTime date);

    @Query(value = "{ 'user_id': ?0 }", delete = true)
    void deleteByUserId(String userId);
}