// ContributorRepository.java
package com.videogamedb.app.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.videogamedb.app.models.Contributor;

import java.util.List;

@Repository
public interface ContributorRepository extends MongoRepository<Contributor, String> {
    List<Contributor> findByType(String type);

    List<Contributor> findByContributorNameContainingIgnoreCase(String name);
}