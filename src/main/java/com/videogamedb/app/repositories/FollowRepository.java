// FollowRepository.java
package com.videogamedb.app.repositories;

import com.videogamedb.app.models.Follow;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends MongoRepository<Follow, String> {
    List<Follow> findByFollowerId(String followerId);

    List<Follow> findByFollowedId(String followedId);

    @Query("{ 'followerId': ?0, 'followedId': ?1 }")
    Follow findByFollowerIdAndFollowedId(String followerId, String followedId);

    @Query(value = "{ 'followerId': ?0 }", count = true)
    long countByFollowerId(String followerId);

    @Query(value = "{ 'followedId': ?0 }", count = true)
    long countByFollowedId(String followedId);
}