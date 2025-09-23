// OwnedGameRepository.java
package com.videogamedb.app.repositories;

import com.videogamedb.app.models.OwnedGame;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OwnedGameRepository extends MongoRepository<OwnedGame, String> {
    List<OwnedGame> findByUserId(String userId);

    List<OwnedGame> findByGameId(String gameId);

    @Query("{ 'userId': ?0, 'gameId': ?1 }")
    OwnedGame findByUserIdAndGameId(String userId, String gameId);

    List<OwnedGame> findByAcquisitionDateAfter(LocalDateTime date);

    @Query(value = "{ 'userId': ?0 }", count = true)
    long countByUserId(String userId);
}