// PlatformService.java
package com.videogamedb.app.services;

import com.videogamedb.app.models.Platform;
import com.videogamedb.app.repositories.PlatformRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlatformService {
    private final PlatformRepository platformRepository;

    public PlatformService(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

    public List<Platform> getAllPlatforms() {
        return platformRepository.findAll();
    }

    public Optional<Platform> getPlatformById(String id) {
        return platformRepository.findById(id);
    }

    public List<Platform> searchPlatformsByName(String name) {
        return platformRepository.findByPlatformNameContainingIgnoreCase(name);
    }

    public Platform getPlatformByName(String name) {
        return platformRepository.findByPlatformName(name);
    }

    public Platform createPlatform(Platform platform) {
        return platformRepository.save(platform);
    }

    public Platform updatePlatform(String id, Platform platformDetails) {
        Optional<Platform> optionalPlatform = platformRepository.findById(id);
        if (optionalPlatform.isPresent()) {
            Platform platform = optionalPlatform.get();
            platform.setPlatformName(platformDetails.getPlatformName());
            return platformRepository.save(platform);
        }
        return null;
    }

    public void deletePlatform(String id) {
        platformRepository.deleteById(id);
    }
}