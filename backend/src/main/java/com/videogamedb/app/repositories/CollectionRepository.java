// CollectionRepository.java
package com.videogamedb.app.repositories;

import com.videogamedb.app.models.Collection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepository extends MongoRepository<Collection, String> {
    List<Collection> findByUserId(String userId);
    List<Collection> findByNameContainingIgnoreCase(String name);
    
    @Query("{ 'games': ?0 }")
    List<Collection> findByGameId(String gameId);
    
    @Query("{ 'userId': ?0, 'games': ?1 }")
    List<Collection> findByUserIdAndGameId(String userId, String gameId);
}