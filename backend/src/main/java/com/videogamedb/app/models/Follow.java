package com.videogamedb.app.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "follows")
public class Follow {
    @Id
    private String id;

    @Field("follower_id")
    private String followerId;

    @Field("followed_id")
    private String followedId;

    // Constructors
    public Follow() {
    }

    public Follow(String followerId, String followedId) {
        this.followerId = followerId;
        this.followedId = followedId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    public String getFollowedId() {
        return followedId;
    }

    public void setFollowedId(String followedId) {
        this.followedId = followedId;
    }
}