// PlatformReleaseController.java
package com.videogamedb.app.controllers;

import com.videogamedb.app.models.PlatformRelease;
import com.videogamedb.app.services.PlatformReleaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/platform-releases")
public class PlatformReleaseController {
    private final PlatformReleaseService platformReleaseService;

    public PlatformReleaseController(PlatformReleaseService platformReleaseService) {
        this.platformReleaseService = platformReleaseService;
    }

    @GetMapping
    public ResponseEntity<List<PlatformRelease>> getAllPlatformReleases() {
        List<PlatformRelease> platformReleases = platformReleaseService.getAllPlatformReleases();
        return ResponseEntity.ok(platformReleases);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlatformRelease> getPlatformReleaseById(@PathVariable String id) {
        Optional<PlatformRelease> platformRelease = platformReleaseService.getPlatformReleaseById(id);
        return platformRelease.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<PlatformRelease>> getPlatformReleasesByGameId(@PathVariable String gameId) {
        List<PlatformRelease> platformReleases = platformReleaseService.getPlatformReleasesByGameId(gameId);
        return ResponseEntity.ok(platformReleases);
    }

    @GetMapping("/platform/{platformId}")
    public ResponseEntity<List<PlatformRelease>> getPlatformReleasesByPlatformId(@PathVariable String platformId) {
        List<PlatformRelease> platformReleases = platformReleaseService.getPlatformReleasesByPlatformId(platformId);
        return ResponseEntity.ok(platformReleases);
    }

    @GetMapping("/game/{gameId}/platform/{platformId}")
    public ResponseEntity<PlatformRelease> getPlatformReleaseByGameIdAndPlatformId(
            @PathVariable String gameId,
            @PathVariable String platformId) {
        PlatformRelease platformRelease = platformReleaseService.getPlatformReleaseByGameIdAndPlatformId(gameId,
                platformId);
        if (platformRelease != null) {
            return ResponseEntity.ok(platformRelease);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/price/max/{maxPrice}")
    public ResponseEntity<List<PlatformRelease>> getPlatformReleasesByMaxPrice(@PathVariable Double maxPrice) {
        List<PlatformRelease> platformReleases = platformReleaseService.getPlatformReleasesByMaxPrice(maxPrice);
        return ResponseEntity.ok(platformReleases);
    }

    @GetMapping("/price/range")
    public ResponseEntity<List<PlatformRelease>> getPlatformReleasesByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        List<PlatformRelease> platformReleases = platformReleaseService.getPlatformReleasesByPriceRange(minPrice,
                maxPrice);
        return ResponseEntity.ok(platformReleases);
    }

    @PostMapping
    public ResponseEntity<PlatformRelease> createPlatformRelease(@RequestBody PlatformRelease platformRelease) {
        PlatformRelease createdPlatformRelease = platformReleaseService.createPlatformRelease(platformRelease);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlatformRelease);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlatformRelease> updatePlatformRelease(
            @PathVariable String id,
            @RequestBody PlatformRelease platformReleaseDetails) {
        PlatformRelease updatedPlatformRelease = platformReleaseService.updatePlatformRelease(id,
                platformReleaseDetails);
        if (updatedPlatformRelease != null) {
            return ResponseEntity.ok(updatedPlatformRelease);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlatformRelease(@PathVariable String id) {
        platformReleaseService.deletePlatformRelease(id);
        return ResponseEntity.noContent().build();
    }
}