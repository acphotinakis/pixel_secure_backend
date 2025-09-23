package com.videogamedb.app.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "ratings")
public class Rating {
    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("game_id")
    private String gameId;

    private Integer rating; // 1-5

    @Field("ratingDate")
    private LocalDateTime ratingDate;

    // Constructors
    public Rating() {
    }

    public Rating(String userId, String gameId, Integer rating, LocalDateTime ratingDate) {
        this.userId = userId;
        this.gameId = gameId;
        this.rating = rating;
        this.ratingDate = ratingDate;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDateTime getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(LocalDateTime ratingDate) {
        this.ratingDate = ratingDate;
    }
}