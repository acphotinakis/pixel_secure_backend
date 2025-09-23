package com.videogamedb.app.models;

import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "accessTimes")
public class AccessTime {
    @Id
    private String id;

    @Field("user_id")
    private String userId;

    private LocalDateTime time;

    // Constructors
    public AccessTime() {
    }

    public AccessTime(String userId, LocalDateTime time) {
        this.userId = userId;
        this.time = time;
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

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}