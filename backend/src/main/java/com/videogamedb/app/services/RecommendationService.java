// package com.videogamedb.app.services;

// import java.util.List;
// import java.util.Optional;
// import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.mongodb.core.MongoTemplate;
// import org.springframework.data.mongodb.core.aggregation.Aggregation;
// import org.springframework.data.mongodb.core.aggregation.AggregationResults;
// import org.springframework.data.mongodb.core.query.Criteria;
// import org.springframework.stereotype.Service;

// import com.videogamedb.app.models.User;
// import com.videogamedb.app.models.VideoGame;
// import com.videogamedb.app.repositories.UserRepository;
// import com.videogamedb.app.repositories.VideoGameRepository;

// @Service
// public class RecommendationService {

//     @Autowired
//     private UserRepository userRepository;

//     @Autowired
//     private VideoGameRepository videoGameRepository;

//     @Autowired
//     private MongoTemplate mongoTemplate;

//     public List<VideoGame> getRecommendedGames(String username) {
//         Optional<User> user = userRepository.findByUsernameEnc(username);
//         if (!user.isPresent()) {
//             return List.of();
//         }

//         // Get user's favorite genres based on play time
//         Aggregation aggregation = Aggregation.newAggregation(
//                 Aggregation.match(Criteria.where("username").is(username)),
//                 Aggregation.unwind("plays"),
//                 Aggregation.lookup("videogames", "plays.vgId", "vgId", "gameDetails"),
//                 Aggregation.unwind("gameDetails"),
//                 Aggregation.unwind("gameDetails.genres"),
//                 Aggregation.group("gameDetails.genres").count().as("count"),
//                 Aggregation.sort(org.springframework.data.domain.Sort
//                         .by(org.springframework.data.domain.Sort.Direction.DESC, "count")),
//                 Aggregation.limit(3));

//         AggregationResults<GenreCount> results = mongoTemplate.aggregate(
//                 aggregation, "users", GenreCount.class);

//         List<String> favoriteGenres = results.getMappedResults().stream()
//                 .map(GenreCount::getId)
//                 .collect(Collectors.toList());

//         // Find games in favorite genres that user hasn't played
//         if (!favoriteGenres.isEmpty()) {
//             return videoGameRepository.findByGenres(favoriteGenres).stream()
//                     .filter(game -> user.get().getPlays().stream()
//                             .noneMatch(play -> play.getVgId().equals(game.getId())))
//                     .limit(10)
//                     .collect(Collectors.toList());
//         }

//         return List.of();
//     }

//     public List<VideoGame> getSimilarUsersGames(String usernameEnc) {
//         Optional<User> user = userRepository.findByUsernameEnc(usernameEnc);
//         if (user.isEmpty()) {
//             return List.of();
//         }

//         // Get encrypted usernames that this user follows
//         List<String> followedUsernamesEnc = user.get().getFollows();
//         if (followedUsernamesEnc.isEmpty()) {
//             return List.of();
//         }

//         // Aggregation pipeline to get popular games among followed users
//         Aggregation aggregation = Aggregation.newAggregation(
//                 // Match followed users by their encrypted usernames
//                 Aggregation.match(Criteria.where("usernameEnc").in(followedUsernamesEnc)),
//                 Aggregation.unwind("plays"),
//                 Aggregation.group("plays.vgId").count().as("playCount"),
//                 Aggregation.sort(org.springframework.data.domain.Sort
//                         .by(org.springframework.data.domain.Sort.Direction.DESC, "playCount")),
//                 Aggregation.limit(10),
//                 // Lookup game details in videogames collection
//                 Aggregation.lookup("videogames", "plays.vgId", "vgId", "gameDetails"),
//                 Aggregation.unwind("gameDetails"),
//                 Aggregation.replaceRoot("gameDetails"));

//         AggregationResults<VideoGame> results = mongoTemplate.aggregate(
//                 aggregation, "users", VideoGame.class);

//         return results.getMappedResults();
//     }

//     // Helper class for aggregation results
//     private static class GenreCount {
//         private String id;
//         private int count;

//         public String getId() {
//             return id;
//         }

//         public void setId(String id) {
//             this.id = id;
//         }

//         public int getCount() {
//             return count;
//         }

//         public void setCount(int count) {
//             this.count = count;
//         }
//     }
// }