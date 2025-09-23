package com.videogamedb.app.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoHealthIndicator implements HealthIndicator {

    private final MongoTemplate mongoTemplate;

    public MongoHealthIndicator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Health health() {
        try {
            // Perform a simple operation to check MongoDB connection
            mongoTemplate.executeCommand("{ ping: 1 }");
            return Health.up().withDetail("database", "MongoDB").build();
        } catch (Exception e) {
            return Health.down().withDetail("database", "MongoDB")
                    .withException(e).build();
        }
    }
}