// AccessTimeRepository.java
package com.videogamedb.app.repositories;

import com.videogamedb.app.models.AccessTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccessTimeRepository extends MongoRepository<AccessTime, String> {
    List<AccessTime> findByUserId(String userId);

    List<AccessTime> findByTimeAfter(LocalDateTime time);

    List<AccessTime> findByTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("{ 'userId': ?0, 'time': { $gte: ?1, $lte: ?2 } }")
    List<AccessTime> findUserAccessInDateRange(String userId, LocalDateTime start, LocalDateTime end);
}