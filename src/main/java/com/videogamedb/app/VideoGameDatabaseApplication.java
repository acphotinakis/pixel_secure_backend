package com.videogamedb.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.videogamedb.app.repositories")
@EnableCaching
public class VideoGameDatabaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoGameDatabaseApplication.class, args);
    }
}