package com.videogamedb.app.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableMongoAuditing(auditorAwareRef = "auditorProvider")
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            try {
                return Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
            } catch (Exception e) {
                return Optional.of("system");
            }
        };
    }
}
