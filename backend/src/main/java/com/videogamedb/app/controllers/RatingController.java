// RatingController.java
package com.videogamedb.app.controllers;

import com.videogamedb.app.models.Rating;
import com.videogamedb.app.services.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public ResponseEntity<List<Rating>> getAllRatings() {
        List<Rating> ratings = ratingService.getAllRatings();
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rating> getRatingById(@PathVariable String id) {
        Optional<Rating> rating = ratingService.getRatingById(id);
        return rating.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Rating>> getRatingsByUserId(@PathVariable String userId) {
        List<Rating> ratings = ratingService.getRatingsByUserId(userId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<Rating>> getRatingsByGameId(@PathVariable String gameId) {
        List<Rating> ratings = ratingService.getRatingsByGameId(gameId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/user/{userId}/game/{gameId}")
    public ResponseEntity<Rating> getRatingByUserIdAndGameId(
            @PathVariable String userId,
            @PathVariable String gameId) {
        Optional<Rating> rating = ratingService.getRatingByUserIdAndGameId(userId, gameId);
        return rating.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/min-rating/{minRating}")
    public ResponseEntity<List<Rating>> getRatingsWithMinRating(@PathVariable Integer minRating) {
        List<Rating> ratings = ratingService.getRatingsWithMinRating(minRating);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/after")
    public ResponseEntity<List<Rating>> getRatingsAfter(@RequestParam LocalDateTime date) {
        List<Rating> ratings = ratingService.getRatingsAfter(date);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/game/{gameId}/ratings-only")
    public ResponseEntity<List<Rating>> getRatingsByGameIdOnlyRating(@PathVariable String gameId) {
        List<Rating> ratings = ratingService.getRatingsByGameIdOnlyRating(gameId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/game/{gameId}/count")
    public ResponseEntity<Long> countRatingsByGameId(@PathVariable String gameId) {
        long count = ratingService.countRatingsByGameId(gameId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/game/{gameId}/rating/{rating}/count")
    public ResponseEntity<Long> countRatingsByGameIdAndRating(
            @PathVariable String gameId,
            @PathVariable Integer rating) {
        long count = ratingService.countRatingsByGameIdAndRating(gameId, rating);
        return ResponseEntity.ok(count);
    }

    @PostMapping
    public ResponseEntity<Rating> createRating(@RequestBody Rating rating) {
        Rating createdRating = ratingService.createRating(rating);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRating);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rating> updateRating(
            @PathVariable String id,
            @RequestBody Rating ratingDetails) {
        Rating updatedRating = ratingService.updateRating(id, ratingDetails);
        if (updatedRating != null) {
            return ResponseEntity.ok(updatedRating);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable String id) {
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}