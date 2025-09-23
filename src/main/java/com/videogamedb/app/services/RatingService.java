// RatingService.java
package com.videogamedb.app.services;

import com.videogamedb.app.models.Rating;
import com.videogamedb.app.repositories.RatingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Optional<Rating> getRatingById(String id) {
        return ratingRepository.findById(id);
    }

    public List<Rating> getRatingsByUserId(String userId) {
        return ratingRepository.findByUserId(userId);
    }

    public List<Rating> getRatingsByGameId(String gameId) {
        return ratingRepository.findByGameId(gameId);
    }

    public Optional<Rating> getRatingByUserIdAndGameId(String userId, String gameId) {
        return ratingRepository.findByUserIdAndGameId(userId, gameId);
    }

    public List<Rating> getRatingsWithMinRating(Integer minRating) {
        return ratingRepository.findByRatingGreaterThanEqual(minRating);
    }

    public List<Rating> getRatingsAfter(LocalDateTime date) {
        return ratingRepository.findByRatingDateAfter(date);
    }

    public List<Rating> getRatingsByGameIdOnlyRating(String gameId) {
        return ratingRepository.findRatingsByGameId(gameId);
    }

    public long countRatingsByGameId(String gameId) {
        return ratingRepository.countByGameId(gameId);
    }

    public long countRatingsByGameIdAndRating(String gameId, Integer rating) {
        return ratingRepository.countByGameIdAndRating(gameId, rating);
    }

    public Rating createRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    public Rating updateRating(String id, Rating ratingDetails) {
        Optional<Rating> optionalRating = ratingRepository.findById(id);
        if (optionalRating.isPresent()) {
            Rating rating = optionalRating.get();
            rating.setUserId(ratingDetails.getUserId());
            rating.setGameId(ratingDetails.getGameId());
            rating.setRating(ratingDetails.getRating());
            rating.setRatingDate(ratingDetails.getRatingDate());
            return ratingRepository.save(rating);
        }
        return null;
    }

    public void deleteRating(String id) {
        ratingRepository.deleteById(id);
    }
}