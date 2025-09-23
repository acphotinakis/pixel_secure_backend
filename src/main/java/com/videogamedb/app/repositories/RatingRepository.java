// RatingRepository.java
package com.videogamedb.app.repositories;

import com.videogamedb.app.models.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends MongoRepository<Rating, String> {
    List<Rating> findByUserId(String userId);
    List<Rating> findByGameId(String gameId);
    
    @Query("{ 'userId': ?0, 'gameId': ?1 }")
    Optional<Rating> findByUserIdAndGameId(String userId, String gameId);
    
    List<Rating> findByRatingGreaterThanEqual(Integer minRating);
    List<Rating> findByRatingDateAfter(LocalDateTime date);
    
    @Query(value = "{ 'gameId': ?0 }", fields = "{ 'rating': 1 }")
    List<Rating> findRatingsByGameId(String gameId);
    
    @Query(value = "{ 'gameId': ?0 }", count = true)
    long countByGameId(String gameId);
    
    @Query(value = "{ 'gameId': ?0, 'rating': ?1 }", count = true)
    long countByGameIdAndRating(String gameId, Integer rating);
}