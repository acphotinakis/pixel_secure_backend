package com.videogamedb.app.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.videogamedb.app.models.User;
import com.videogamedb.app.models.VideoGame;

public interface CustomGameRepository extends MongoRepository<VideoGame, String> {
    
    // Get game recommendations for a user based on their play history
    @Aggregation(pipeline = {
        "// First, get the user's favorite genres",
        "{ $lookup: { from: 'users', localField: '?_id', foreignField: 'username', as: 'user' } }",
        "{ $unwind: '$user' }",
        "{ $unwind: '$user.plays' }",
        "{ $lookup: { from: 'videogames', localField: 'user.plays.vgId', foreignField: 'vgId', as: 'playedGames' } }",
        "{ $unwind: '$playedGames' }",
        "{ $unwind: '$playedGames.genres' }",
        "{ $group: { _id: '$playedGames.genres', count: { $sum: 1 } } }",
        "{ $sort: { count: -1 } }",
        "{ $limit: 3 }",
        "// Then, find games in those genres that the user hasn't played",
        "{ $lookup: { from: 'videogames', localField: '_id', foreignField: 'genres', as: 'recommendedGames' } }",
        "{ $unwind: '$recommendedGames' }",
        "{ $match: { 'recommendedGames.vgId': { $nin: '$user.plays.vgId' } } }",
        "{ $group: { _id: '$recommendedGames.vgId', game: { $first: '$recommendedGames' } } }",
        "{ $replaceRoot: { newRoot: '$game' } }",
        "{ $limit: 10 }"
    })
    List<VideoGame> getRecommendedGames(String username);
    
    // Get similar users based on game preferences
    @Aggregation(pipeline = {
        "// Find users who play similar games",
        "{ $lookup: { from: 'users', localField: '?_id', foreignField: 'username', as: 'currentUser' } }",
        "{ $unwind: '$currentUser' }",
        "{ $unwind: '$currentUser.plays' }",
        "{ $lookup: { from: 'users', localField: 'currentUser.plays.vgId', foreignField: 'plays.vgId', as: 'similarUsers' } }",
        "{ $unwind: '$similarUsers' }",
        "{ $match: { 'similarUsers.username': { $ne: '?0' } } }",
        "{ $group: { _id: '$similarUsers.username', commonGames: { $sum: 1 } } }",
        "{ $sort: { commonGames: -1 } }",
        "{ $limit: 5 }"
    })
    List<User> findSimilarUsers(String username);
}