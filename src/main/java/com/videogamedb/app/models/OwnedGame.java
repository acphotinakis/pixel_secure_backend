package com.videogamedb.app.models;

import org.springframework.data.mongodb.core.mapping.Field;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "owned")
public class OwnedGame {
    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("game_id")
    private String gameId;

    @Field("acquisitionDate")
    private LocalDateTime acquisitionDate;

    // Constructors
    public OwnedGame() {
    }

    public OwnedGame(String userId, String gameId, LocalDateTime acquisitionDate) {
        this.userId = userId;
        this.gameId = gameId;
        this.acquisitionDate = acquisitionDate;
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

    public LocalDateTime getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDateTime acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }
}