package com.videogamedb.app.config;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadConcern;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@EnableMongoRepositories(basePackages = "com.videogamedb.app.repositories")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String connectionString;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    public MongoClient mongoClient() {
        ConnectionString connString = new ConnectionString(connectionString);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .readConcern(ReadConcern.LOCAL)
                .writeConcern(WriteConcern.MAJORITY)
                .build();
        return MongoClients.create(settings);
    }

    @Override
    public Collection<String> getMappingBasePackages() {
        return Collections.singleton("com.videogamedb.app");
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoCustomConversions conversions) {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient(), getDatabaseName());
        mongoTemplate.setWriteConcern(WriteConcern.MAJORITY);
        return mongoTemplate;
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }

    @Override
    public MongoCustomConversions customConversions() {
        return new ConverterConfig().mongoCustomConversions();
    }
}
