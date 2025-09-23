// OwnedGameService.java
package com.videogamedb.app.services;

import com.videogamedb.app.models.OwnedGame;
import com.videogamedb.app.repositories.OwnedGameRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OwnedGameService {
    private final OwnedGameRepository ownedGameRepository;

    public OwnedGameService(OwnedGameRepository ownedGameRepository) {
        this.ownedGameRepository = ownedGameRepository;
    }

    public List<OwnedGame> getAllOwnedGames() {
        return ownedGameRepository.findAll();
    }

    public Optional<OwnedGame> getOwnedGameById(String id) {
        return ownedGameRepository.findById(id);
    }

    public List<OwnedGame> getOwnedGamesByUserId(String userId) {
        return ownedGameRepository.findByUserId(userId);
    }

    public List<OwnedGame> getOwnedGamesByGameId(String gameId) {
        return ownedGameRepository.findByGameId(gameId);
    }

    public OwnedGame getOwnedGameByUserIdAndGameId(String userId, String gameId) {
        return ownedGameRepository.findByUserIdAndGameId(userId, gameId);
    }

    public List<OwnedGame> getOwnedGamesAcquiredAfter(LocalDateTime date) {
        return ownedGameRepository.findByAcquisitionDateAfter(date);
    }

    public long countOwnedGamesByUserId(String userId) {
        return ownedGameRepository.countByUserId(userId);
    }

    public OwnedGame createOwnedGame(OwnedGame ownedGame) {
        return ownedGameRepository.save(ownedGame);
    }

    public OwnedGame updateOwnedGame(String id, OwnedGame ownedGameDetails) {
        Optional<OwnedGame> optionalOwnedGame = ownedGameRepository.findById(id);
        if (optionalOwnedGame.isPresent()) {
            OwnedGame ownedGame = optionalOwnedGame.get();
            ownedGame.setUserId(ownedGameDetails.getUserId());
            ownedGame.setGameId(ownedGameDetails.getGameId());
            ownedGame.setAcquisitionDate(ownedGameDetails.getAcquisitionDate());
            return ownedGameRepository.save(ownedGame);
        }
        return null;
    }

    public void deleteOwnedGame(String id) {
        ownedGameRepository.deleteById(id);
    }
}