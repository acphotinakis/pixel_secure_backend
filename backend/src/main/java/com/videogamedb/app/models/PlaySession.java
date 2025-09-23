package com.videogamedb.app.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "plays")
public class PlaySession {
    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("game_id")
    private String gameId;

    @Field("datetimeOpened")
    private LocalDateTime datetimeOpened;

    @Field("timePlayed")
    private Integer timePlayed; // in seconds

    // Constructors
    public PlaySession() {
    }

    public PlaySession(String userId, String gameId, LocalDateTime datetimeOpened, Integer timePlayed) {
        this.userId = userId;
        this.gameId = gameId;
        this.datetimeOpened = datetimeOpened;
        this.timePlayed = timePlayed;
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

    public LocalDateTime getDatetimeOpened() {
        return datetimeOpened;
    }

    public void setDatetimeOpened(LocalDateTime datetimeOpened) {
        this.datetimeOpened = datetimeOpened;
    }

    public Integer getTimePlayed() {
        return timePlayed;
    }

    public void setTimePlayed(Integer timePlayed) {
        this.timePlayed = timePlayed;
    }
}