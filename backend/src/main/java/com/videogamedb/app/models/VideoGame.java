package com.videogamedb.app.models;

import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

@Document(collection = "videogames")
public class VideoGame {
    @Id
    private String id;

    private String title;
    private String esrb;
    private List<String> developers;
    private List<String> publishers;
    private List<String> genres;

    // Constructors
    public VideoGame() {
    }

    public VideoGame(String title, String esrb, List<String> developers,
            List<String> publishers, List<String> genres) {
        this.title = title;
        this.esrb = esrb;
        this.developers = developers;
        this.publishers = publishers;
        this.genres = genres;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEsrb() {
        return esrb;
    }

    public void setEsrb(String esrb) {
        this.esrb = esrb;
    }

    public List<String> getDevelopers() {
        return developers;
    }

    public void setDevelopers(List<String> developers) {
        this.developers = developers;
    }

    public List<String> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<String> publishers) {
        this.publishers = publishers;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}
