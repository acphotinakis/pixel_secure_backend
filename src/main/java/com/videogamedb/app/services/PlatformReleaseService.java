// PlatformReleaseService.java
package com.videogamedb.app.services;

import com.videogamedb.app.models.PlatformRelease;
import com.videogamedb.app.repositories.PlatformReleaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlatformReleaseService {
    private final PlatformReleaseRepository platformReleaseRepository;

    public PlatformReleaseService(PlatformReleaseRepository platformReleaseRepository) {
        this.platformReleaseRepository = platformReleaseRepository;
    }

    public List<PlatformRelease> getAllPlatformReleases() {
        return platformReleaseRepository.findAll();
    }

    public Optional<PlatformRelease> getPlatformReleaseById(String id) {
        return platformReleaseRepository.findById(id);
    }

    public List<PlatformRelease> getPlatformReleasesByGameId(String gameId) {
        return platformReleaseRepository.findByGameId(gameId);
    }

    public List<PlatformRelease> getPlatformReleasesByPlatformId(String platformId) {
        return platformReleaseRepository.findByPlatformId(platformId);
    }

    public PlatformRelease getPlatformReleaseByGameIdAndPlatformId(String gameId, String platformId) {
        return platformReleaseRepository.findByGameIdAndPlatformId(gameId, platformId);
    }

    public List<PlatformRelease> getPlatformReleasesByMaxPrice(Double maxPrice) {
        return platformReleaseRepository.findByPriceLessThanEqual(maxPrice);
    }

    public List<PlatformRelease> getPlatformReleasesByPriceRange(Double minPrice, Double maxPrice) {
        return platformReleaseRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public PlatformRelease createPlatformRelease(PlatformRelease platformRelease) {
        return platformReleaseRepository.save(platformRelease);
    }

    public PlatformRelease updatePlatformRelease(String id, PlatformRelease platformReleaseDetails) {
        Optional<PlatformRelease> optionalPlatformRelease = platformReleaseRepository.findById(id);
        if (optionalPlatformRelease.isPresent()) {
            PlatformRelease platformRelease = optionalPlatformRelease.get();
            platformRelease.setGameId(platformReleaseDetails.getGameId());
            platformRelease.setPlatformId(platformReleaseDetails.getPlatformId());
            platformRelease.setPrice(platformReleaseDetails.getPrice());
            platformRelease.setReleaseDate(platformReleaseDetails.getReleaseDate());
            return platformReleaseRepository.save(platformRelease);
        }
        return null;
    }

    public void deletePlatformRelease(String id) {
        platformReleaseRepository.deleteById(id);
    }
}