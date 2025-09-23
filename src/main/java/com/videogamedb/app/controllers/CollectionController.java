// CollectionController.java
package com.videogamedb.app.controllers;

import com.videogamedb.app.models.Collection;
import com.videogamedb.app.services.CollectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {
    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping
    public ResponseEntity<List<Collection>> getAllCollections() {
        List<Collection> collections = collectionService.getAllCollections();
        return ResponseEntity.ok(collections);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Collection> getCollectionById(@PathVariable String id) {
        Optional<Collection> collection = collectionService.getCollectionById(id);
        return collection.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Collection>> getCollectionsByUserId(@PathVariable String userId) {
        List<Collection> collections = collectionService.getCollectionsByUserId(userId);
        return ResponseEntity.ok(collections);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Collection>> searchCollectionsByName(@RequestParam String name) {
        List<Collection> collections = collectionService.searchCollectionsByName(name);
        return ResponseEntity.ok(collections);
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<Collection>> getCollectionsByGameId(@PathVariable String gameId) {
        List<Collection> collections = collectionService.getCollectionsByGameId(gameId);
        return ResponseEntity.ok(collections);
    }

    @GetMapping("/user/{userId}/game/{gameId}")
    public ResponseEntity<List<Collection>> getCollectionsByUserIdAndGameId(
            @PathVariable String userId,
            @PathVariable String gameId) {
        List<Collection> collections = collectionService.getCollectionsByUserIdAndGameId(userId, gameId);
        return ResponseEntity.ok(collections);
    }

    @PostMapping
    public ResponseEntity<Collection> createCollection(@RequestBody Collection collection) {
        Collection createdCollection = collectionService.createCollection(collection);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCollection);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Collection> updateCollection(
            @PathVariable String id,
            @RequestBody Collection collectionDetails) {
        Collection updatedCollection = collectionService.updateCollection(id, collectionDetails);
        if (updatedCollection != null) {
            return ResponseEntity.ok(updatedCollection);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable String id) {
        collectionService.deleteCollection(id);
        return ResponseEntity.noContent().build();
    }
}