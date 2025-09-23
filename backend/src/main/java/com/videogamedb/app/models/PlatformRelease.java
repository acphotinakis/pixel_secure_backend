package com.videogamedb.app.models;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "platformReleases")
public class PlatformRelease {
    @Id
    private String id;

    @Field("game_id")
    private String gameId;

    @Field("platform_id")
    private String platformId;

    private Double price;

    @Field("releaseDate")
    private LocalDateTime releaseDate;

    // Constructors
    public PlatformRelease() {
    }

    public PlatformRelease(String gameId, String platformId, Double price, LocalDateTime releaseDate) {
        this.gameId = gameId;
        this.platformId = platformId;
        this.price = price;
        this.releaseDate = releaseDate;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }
}
