package com.videogamedb.app.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.videogamedb.app.models.Platform;

public interface PlatformRepository extends MongoRepository<Platform, String> {

    List<Platform> findByPlatformNameContainingIgnoreCase(String name);

    Platform findByPlatformName(String name);

    // Find platforms with games released after a specific date
    @Query("{ 'games.platformReleases.releaseDate': { $gt: ?0 } }")
    List<Platform> findPlatformsWithGamesAfter(String date);

    // // Find platforms with average game price less than specified
    // @Aggregation(pipeline = {
    //         "{ $lookup: { from: 'videogames', localField: 'platformName', foreignField: 'platformReleases.platformName', as: 'games' } }",
    //         "{ $unwind: '$games' }",
    //         "{ $unwind: '$games.platformReleases' }",
    //         "{ $match: { 'games.platformReleases.platformName': '$platformName' } }",
    //         "{ $group: { _id: '$_id', avgPrice: { $avg: '$games.platformReleases.price' } } }",
    //         "{ $match: { avgPrice: { $lte: ?0 } } }"
    // })
    // List<Platform> findPlatformsWithAvgPriceLessThan(Double maxAvgPrice);
}