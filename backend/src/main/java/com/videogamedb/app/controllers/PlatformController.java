// PlatformController.java
package com.videogamedb.app.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.videogamedb.app.models.Platform;
import com.videogamedb.app.services.PlatformService;

@RestController
@RequestMapping("/api/platforms")
public class PlatformController {
    private final PlatformService platformService;

    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @GetMapping
    public ResponseEntity<List<Platform>> getAllPlatforms() {
        List<Platform> platforms = platformService.getAllPlatforms();
        return ResponseEntity.ok(platforms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Platform> getPlatformById(@PathVariable String id) {
        Optional<Platform> platform = platformService.getPlatformById(id);
        return platform.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Platform>> searchPlatformsByName(@RequestParam String name) {
        List<Platform> platforms = platformService.searchPlatformsByName(name);
        return ResponseEntity.ok(platforms);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Platform> getPlatformByName(@PathVariable String name) {
        Platform platform = platformService.getPlatformByName(name);
        if (platform != null) {
            return ResponseEntity.ok(platform);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Platform> createPlatform(@RequestBody Platform platform) {
        Platform createdPlatform = platformService.createPlatform(platform);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlatform);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Platform> updatePlatform(
            @PathVariable String id,
            @RequestBody Platform platformDetails) {
        Platform updatedPlatform = platformService.updatePlatform(id, platformDetails);
        if (updatedPlatform != null) {
            return ResponseEntity.ok(updatedPlatform);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlatform(@PathVariable String id) {
        platformService.deletePlatform(id);
        return ResponseEntity.noContent().build();
    }
}