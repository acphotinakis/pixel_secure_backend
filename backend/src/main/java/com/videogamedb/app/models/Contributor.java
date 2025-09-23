package com.videogamedb.app.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "contributors")
public class Contributor {
    @Id
    private String id;

    @Field("contributor_name")
    private String contributorName;

    private String type; // "developer" or "publisher"

    // Constructors
    public Contributor() {
    }

    public Contributor(String contributorName, String type) {
        this.contributorName = contributorName;
        this.type = type;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContributorName() {
        return contributorName;
    }

    public void setContributorName(String contributorName) {
        this.contributorName = contributorName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
