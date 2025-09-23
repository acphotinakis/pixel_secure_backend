package com.videogamedb.app.models;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;

public class GameRating {
    @Field("vg_id")
    private String vgId;

    private Integer rating;

    @Field("rating_date")
    private Date ratingDate;

    // Constructors
    public GameRating() {
    }

    public GameRating(String vgId, Integer rating, Date ratingDate) {
        this.vgId = vgId;
        this.rating = rating;
        this.ratingDate = ratingDate;
    }

    // Getters and Setters
    public String getVgId() {
        return vgId;
    }

    public void setVgId(String vgId) {
        this.vgId = vgId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Date getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(Date ratingDate) {
        this.ratingDate = ratingDate;
    }
}