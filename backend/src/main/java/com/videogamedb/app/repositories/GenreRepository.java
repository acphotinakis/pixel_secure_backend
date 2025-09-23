package com.videogamedb.app.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.videogamedb.app.models.Genre;

public interface GenreRepository extends MongoRepository<Genre, String> {

    List<Genre> findByGenreNameContainingIgnoreCase(String name);

    Genre findByGenreName(String name);

    // // Find most popular genres by game count
    // @Aggregation(pipeline = {
    //         "{ $lookup: { from: 'videogames', localField: 'genreName', foreignField: 'genres', as: 'games' } }",
    //         "{ $project: { genreName: 1, gameCount: { $size: '$games' } } }",
    //         "{ $sort: { gameCount: -1 } }",
    //         "{ $limit: ?0 }"
    // })
    // List<Genre> findMostPopularGenres(int limit);

    // // Find genres with the highest average rating
    // @Aggregation(pipeline = {
    //         "{ $lookup: { from: 'videogames', localField: 'genreName', foreignField: 'genres', as: 'games' } }",
    //         "{ $unwind: '$games' }",
    //         "{ $lookup: { from: 'users', localField: 'games.vgId', foreignField: 'ratings.vgId', as: 'ratings' } }",
    //         "{ $unwind: '$ratings' }",
    //         "{ $unwind: '$ratings.ratings' }",
    //         "{ $match: { 'ratings.ratings.vgId': '$games.vgId' } }",
    //         "{ $group: { _id: '$genreName', avgRating: { $avg: '$ratings.ratings.rating' } } }",
    //         "{ $sort: { avgRating: -1 } }",
    //         "{ $limit: ?0 }"
    // })
    // List<Genre> findHighestRatedGenres(int limit);
}