// PlaySessionRepository.java
package com.videogamedb.app.repositories;

import com.videogamedb.app.models.PlaySession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlaySessionRepository extends MongoRepository<PlaySession, String> {
    List<PlaySession> findByUserId(String userId);

    List<PlaySession> findByGameId(String gameId);

    @Query("{ 'userId': ?0, 'gameId': ?1 }")
    List<PlaySession> findByUserIdAndGameId(String userId, String gameId);

    List<PlaySession> findByDatetimeOpenedAfter(LocalDateTime date);

    List<PlaySession> findByDatetimeOpenedBetween(LocalDateTime start, LocalDateTime end);

    @Query(value = "{ 'userId': ?0, 'gameId': ?1 }", count = true)
    long countByUserIdAndGameId(String userId, String gameId);

    @Query(value = "{ 'userId': ?0 }", sort = "{ 'datetimeOpened': -1 }")
    List<PlaySession> findLatestByUserId(String userId, org.springframework.data.domain.Pageable pageable);
}