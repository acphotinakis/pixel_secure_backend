// CollectionService.java
package com.videogamedb.app.services;

import com.videogamedb.app.models.Collection;
import com.videogamedb.app.repositories.CollectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CollectionService {
    private final CollectionRepository collectionRepository;

    public CollectionService(CollectionRepository collectionRepository) {
        this.collectionRepository = collectionRepository;
    }

    public List<Collection> getAllCollections() {
        return collectionRepository.findAll();
    }

    public Optional<Collection> getCollectionById(String id) {
        return collectionRepository.findById(id);
    }

    public List<Collection> getCollectionsByUserId(String userId) {
        return collectionRepository.findByUserId(userId);
    }

    public List<Collection> searchCollectionsByName(String name) {
        return collectionRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Collection> getCollectionsByGameId(String gameId) {
        return collectionRepository.findByGameId(gameId);
    }

    public List<Collection> getCollectionsByUserIdAndGameId(String userId, String gameId) {
        return collectionRepository.findByUserIdAndGameId(userId, gameId);
    }

    public Collection createCollection(Collection collection) {
        return collectionRepository.save(collection);
    }

    public Collection updateCollection(String id, Collection collectionDetails) {
        Optional<Collection> optionalCollection = collectionRepository.findById(id);
        if (optionalCollection.isPresent()) {
            Collection collection = optionalCollection.get();
            collection.setName(collectionDetails.getName());
            collection.setDescription(collectionDetails.getDescription());
            collection.setUserId(collectionDetails.getUserId());
            collection.setGames(collectionDetails.getGames());
            return collectionRepository.save(collection);
        }
        return null;
    }

    public void deleteCollection(String id) {
        collectionRepository.deleteById(id);
    }
}