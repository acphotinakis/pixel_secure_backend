// PlatformReleaseRepository.java
package com.videogamedb.app.repositories;

import com.videogamedb.app.models.PlatformRelease;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatformReleaseRepository extends MongoRepository<PlatformRelease, String> {
    List<PlatformRelease> findByGameId(String gameId);
    List<PlatformRelease> findByPlatformId(String platformId);
    
    @Query("{ 'gameId': ?0, 'platformId': ?1 }")
    PlatformRelease findByGameIdAndPlatformId(String gameId, String platformId);
    
    List<PlatformRelease> findByPriceLessThanEqual(Double maxPrice);
    List<PlatformRelease> findByPriceBetween(Double minPrice, Double maxPrice);
}