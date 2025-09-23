package com.videogamedb.app.config;

import com.videogamedb.app.models.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import jakarta.annotation.PostConstruct;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class IndexConfig {

        private final MongoMappingContext mongoMappingContext;
        private final MongoTemplate mongoTemplate;

        public IndexConfig(MongoMappingContext mongoMappingContext, MongoTemplate mongoTemplate) {
                this.mongoMappingContext = mongoMappingContext;
                this.mongoTemplate = mongoTemplate;
        }

        @PostConstruct
        public void initIndexes() {
                // Automatically apply @Indexed annotations in models (if any)
                IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);

                // Apply indexing for all model classes
                resolver.resolveIndexFor(User.class)
                                .forEach(index -> mongoTemplate.indexOps(User.class).ensureIndex(index));
                resolver.resolveIndexFor(VideoGame.class)
                                .forEach(index -> mongoTemplate.indexOps(VideoGame.class).ensureIndex(index));
                resolver.resolveIndexFor(Contributor.class)
                                .forEach(index -> mongoTemplate.indexOps(Contributor.class).ensureIndex(index));
                resolver.resolveIndexFor(Platform.class)
                                .forEach(index -> mongoTemplate.indexOps(Platform.class).ensureIndex(index));
                resolver.resolveIndexFor(Genre.class)
                                .forEach(index -> mongoTemplate.indexOps(Genre.class).ensureIndex(index));
                resolver.resolveIndexFor(PlatformRelease.class)
                                .forEach(index -> mongoTemplate.indexOps(PlatformRelease.class).ensureIndex(index));
                resolver.resolveIndexFor(OwnedGame.class)
                                .forEach(index -> mongoTemplate.indexOps(OwnedGame.class).ensureIndex(index));
                resolver.resolveIndexFor(PlaySession.class)
                                .forEach(index -> mongoTemplate.indexOps(PlaySession.class).ensureIndex(index));
                resolver.resolveIndexFor(Rating.class)
                                .forEach(index -> mongoTemplate.indexOps(Rating.class).ensureIndex(index));
                resolver.resolveIndexFor(AccessTime.class)
                                .forEach(index -> mongoTemplate.indexOps(AccessTime.class).ensureIndex(index));
                resolver.resolveIndexFor(Follow.class)
                                .forEach(index -> mongoTemplate.indexOps(Follow.class).ensureIndex(index));
                resolver.resolveIndexFor(Collection.class)
                                .forEach(index -> mongoTemplate.indexOps(Collection.class).ensureIndex(index));

                // Apply custom indexes
                createCustomIndexes();
        }

        private void createCustomIndexes() {
                // ----------------------
                // User indexes
                // ----------------------
                // Unique indexes on encrypted fields
                mongoTemplate.indexOps(User.class)
                                .ensureIndex(new Index().on("username_enc", Sort.Direction.ASC).unique());
                mongoTemplate.indexOps(User.class)
                                .ensureIndex(new Index().on("email_enc", Sort.Direction.ASC).unique());
                mongoTemplate.indexOps(User.class)
                                .ensureIndex(new Index().on("audit_token", Sort.Direction.ASC));

                // ----------------------
                // VideoGame indexes
                // ----------------------
                mongoTemplate.indexOps(VideoGame.class)
                                .ensureIndex(new Index().on("title", Sort.Direction.ASC));
                mongoTemplate.indexOps(VideoGame.class)
                                .ensureIndex(new Index().on("developers", Sort.Direction.ASC));
                mongoTemplate.indexOps(VideoGame.class)
                                .ensureIndex(new Index().on("publishers", Sort.Direction.ASC));
                mongoTemplate.indexOps(VideoGame.class)
                                .ensureIndex(new Index().on("genres", Sort.Direction.ASC));

                // ----------------------
                // Contributor indexes
                // ----------------------
                mongoTemplate.indexOps(Contributor.class)
                                .ensureIndex(new Index().on("contributor_name", Sort.Direction.ASC));
                mongoTemplate.indexOps(Contributor.class)
                                .ensureIndex(new Index().on("type", Sort.Direction.ASC));

                // ----------------------
                // Platform indexes
                // ----------------------
                mongoTemplate.indexOps(Platform.class)
                                .ensureIndex(new Index().on("platform_name", Sort.Direction.ASC).unique());

                // ----------------------
                // Genre indexes
                // ----------------------
                mongoTemplate.indexOps(Genre.class)
                                .ensureIndex(new Index().on("genre_name", Sort.Direction.ASC).unique());

                // ----------------------
                // PlatformRelease indexes
                // ----------------------
                mongoTemplate.indexOps(PlatformRelease.class)
                                .ensureIndex(new Index().on("game_id", Sort.Direction.ASC));
                mongoTemplate.indexOps(PlatformRelease.class)
                                .ensureIndex(new Index().on("platform_id", Sort.Direction.ASC));

                // ----------------------
                // OwnedGame indexes
                // ----------------------
                mongoTemplate.indexOps(OwnedGame.class)
                                .ensureIndex(new Index().on("user_id", Sort.Direction.ASC));
                mongoTemplate.indexOps(OwnedGame.class)
                                .ensureIndex(new Index().on("game_id", Sort.Direction.ASC));

                // ----------------------
                // PlaySession indexes
                // ----------------------
                mongoTemplate.indexOps(PlaySession.class)
                                .ensureIndex(new Index().on("user_id", Sort.Direction.ASC).on("game_id",
                                                Sort.Direction.ASC));
                mongoTemplate.indexOps(PlaySession.class)
                                .ensureIndex(new Index().on("datetimeOpened", Sort.Direction.DESC));

                // ----------------------
                // Rating indexes
                // ----------------------
                mongoTemplate.indexOps(Rating.class)
                                .ensureIndex(new Index().on("user_id", Sort.Direction.ASC).on("game_id",
                                                Sort.Direction.ASC));
                mongoTemplate.indexOps(Rating.class)
                                .ensureIndex(new Index().on("ratingDate", Sort.Direction.DESC));

                // ----------------------
                // AccessTime indexes
                // ----------------------
                mongoTemplate.indexOps(AccessTime.class)
                                .ensureIndex(new Index().on("user_id", Sort.Direction.ASC));
                mongoTemplate.indexOps(AccessTime.class)
                                .ensureIndex(new Index().on("time", Sort.Direction.DESC).expire(365, TimeUnit.DAYS));

                // ----------------------
                // Follow indexes
                // ----------------------
                mongoTemplate.indexOps(Follow.class)
                                .ensureIndex(new Index().on("follower_id", Sort.Direction.ASC));
                mongoTemplate.indexOps(Follow.class)
                                .ensureIndex(new Index().on("followed_id", Sort.Direction.ASC));

                // ----------------------
                // Collection indexes
                // ----------------------
                mongoTemplate.indexOps(Collection.class)
                                .ensureIndex(new Index().on("user_id", Sort.Direction.ASC));
                mongoTemplate.indexOps(Collection.class)
                                .ensureIndex(new Index().on("collection_id", Sort.Direction.ASC));

                System.out.println("✅ Indexes created for all collections.");
        }
}
// package com.videogamedb.app.config;

// import com.videogamedb.app.models.User;
// import com.videogamedb.app.models.VideoGame;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.domain.Sort;
// import org.springframework.data.mongodb.core.index.Index;
// import org.springframework.data.mongodb.core.index.IndexResolver;
// import
// org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
// import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

// import jakarta.annotation.PostConstruct;

// @Configuration
// public class IndexConfig {

// private final MongoMappingContext mongoMappingContext;
// private final org.springframework.data.mongodb.core.MongoTemplate
// mongoTemplate;

// public IndexConfig(MongoMappingContext mongoMappingContext,
// org.springframework.data.mongodb.core.MongoTemplate mongoTemplate) {
// this.mongoMappingContext = mongoMappingContext;
// this.mongoTemplate = mongoTemplate;
// }

// @PostConstruct
// public void initIndexes() {
// // Automatically apply @Indexed annotations in models (if any)
// IndexResolver resolver = new
// MongoPersistentEntityIndexResolver(mongoMappingContext);

// resolver.resolveIndexFor(User.class)
// .forEach(index -> mongoTemplate.indexOps(User.class).ensureIndex(index));

// resolver.resolveIndexFor(VideoGame.class)
// .forEach(index ->
// mongoTemplate.indexOps(VideoGame.class).ensureIndex(index));

// // Apply custom indexes
// createCustomIndexes();
// }

// private void createCustomIndexes() {
// // ----------------------
// // User indexes
// // ----------------------
// // Unique indexes on encrypted fields
// mongoTemplate.indexOps(User.class)
// .ensureIndex(new Index().on("username_enc", Sort.Direction.ASC).unique());
// mongoTemplate.indexOps(User.class)
// .ensureIndex(new Index().on("email_enc", Sort.Direction.ASC).unique());

// // ----------------------
// // VideoGame indexes
// // ----------------------
// mongoTemplate.indexOps(VideoGame.class)
// .ensureIndex(new Index().on("title", Sort.Direction.ASC));
// mongoTemplate.indexOps(VideoGame.class)
// .ensureIndex(new Index().on("genres", Sort.Direction.ASC));
// mongoTemplate.indexOps(VideoGame.class)
// .ensureIndex(new Index().on("esrb", Sort.Direction.ASC));

// // ----------------------
// // Optional text index for search
// // Only include 'description' if added to VideoGame model
// // ----------------------
// // mongoTemplate.indexOps(VideoGame.class)
// // .ensureIndex(new Index()
// // .on("title", Sort.Direction.ASC)
// // .on("description", Sort.Direction.ASC));
// }
// }
