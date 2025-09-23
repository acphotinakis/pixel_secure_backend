package com.videogamedb.app.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.videogamedb.app.models.VideoGame;

public interface VideoGameRepository extends MongoRepository<VideoGame, String> {
        List<VideoGame> findByTitleContainingIgnoreCase(String title);

        List<VideoGame> findByEsrb(String esrbRating);

        @Query("{ 'developers': ?0 }")
        List<VideoGame> findByDeveloperId(String developerId);

        @Query("{ 'publishers': ?0 }")
        List<VideoGame> findByPublisherId(String publisherId);

        @Query("{ 'genres': ?0 }")
        List<VideoGame> findByGenreId(String genreId);

        @Query("{ 'title': { $regex: ?0, $options: 'i' }, 'esrb': ?1 }")
        List<VideoGame> findByTitleAndEsrb(String title, String esrb);

        @Query(value = "{ 'developers': ?0 }", count = true)
        long countByDeveloperId(String developerId);

        @Query(value = "{ 'publishers': ?0 }", count = true)
        long countByPublisherId(String publisherId);

        // Find games by genre
        @Query("{ 'genres': ?0 }")
        List<VideoGame> findByGenre(String genre);

        // Find games by multiple genres
        @Query("{ 'genres': { $all: ?0 } }")
        List<VideoGame> findByGenres(List<String> genres);

        // Find games released on a specific platform
        @Query("{ 'platformReleases.platformName': ?0 }")
        List<VideoGame> findByPlatform(String platformName);

        // Find games developed by a specific contributor
        @Query("{ 'developers.contributorId': ?0 }")
        List<VideoGame> findByDeveloper(String contributorId);

        // Find games published by a specific contributor
        @Query("{ 'publishers.contributorId': ?0 }")
        List<VideoGame> findByPublisher(String contributorId);

        // Find games with price less than or equal to a value
        @Query("{ 'platformReleases.price': { $lte: ?0 } }")
        List<VideoGame> findByMaxPrice(Double maxPrice);

        // Find games released after a specific date
        @Query("{ 'platformReleases.releaseDate': { $gt: ?0 } }")
        List<VideoGame> findByReleaseAfter(String date);

        // // Find top rated games
        // @Aggregation(pipeline = {
        // "{ $lookup: { from: 'users', localField: 'vgId', foreignField:
        // 'ratings.vgId', as: 'userRatings' } }",
        // "{ $unwind: '$userRatings' }",
        // "{ $unwind: '$userRatings.ratings' }",
        // "{ $match: { 'userRatings.ratings.vgId': '$_id' } }",
        // "{ $group: { _id: '$_id', avgRating: { $avg: '$userRatings.ratings.rating' }
        // } }",
        // "{ $sort: { avgRating: -1 } }",
        // "{ $limit: ?0 }"
        // })
        // List<VideoGame> findTopRatedGames(int limit);

        // // Find most played games
        // @Aggregation(pipeline = {
        // "{ $lookup: { from: 'users', localField: 'vgId', foreignField: 'plays.vgId',
        // as: 'userPlays' } }",
        // "{ $unwind: '$userPlays' }",
        // "{ $unwind: '$userPlays.plays' }",
        // "{ $match: { 'userPlays.plays.vgId': '$_id' } }",
        // "{ $group: { _id: '$_id', totalPlayTime: { $sum:
        // '$userPlays.plays.timePlayed' } } }",
        // "{ $sort: { totalPlayTime: -1 } }",
        // "{ $limit: ?0 }"
        // })
        // List<VideoGame> findMostPlayedGames(int limit);
}