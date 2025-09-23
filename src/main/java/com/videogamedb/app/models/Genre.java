package com.videogamedb.app.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "genres")
public class Genre {
    @Id
    private String id;

    @Field("genre_name")
    private String genreName;

    // Constructors
    public Genre() {
    }

    public Genre(String genreName) {
        this.genreName = genreName;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}
