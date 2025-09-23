// OwnedGameController.java
package com.videogamedb.app.controllers;

import com.videogamedb.app.models.OwnedGame;
import com.videogamedb.app.services.OwnedGameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/owned-games")
public class OwnedGameController {
    private final OwnedGameService ownedGameService;

    public OwnedGameController(OwnedGameService ownedGameService) {
        this.ownedGameService = ownedGameService;
    }

    @GetMapping
    public ResponseEntity<List<OwnedGame>> getAllOwnedGames() {
        List<OwnedGame> ownedGames = ownedGameService.getAllOwnedGames();
        return ResponseEntity.ok(ownedGames);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OwnedGame> getOwnedGameById(@PathVariable String id) {
        Optional<OwnedGame> ownedGame = ownedGameService.getOwnedGameById(id);
        return ownedGame.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OwnedGame>> getOwnedGamesByUserId(@PathVariable String userId) {
        List<OwnedGame> ownedGames = ownedGameService.getOwnedGamesByUserId(userId);
        return ResponseEntity.ok(ownedGames);
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<OwnedGame>> getOwnedGamesByGameId(@PathVariable String gameId) {
        List<OwnedGame> ownedGames = ownedGameService.getOwnedGamesByGameId(gameId);
        return ResponseEntity.ok(ownedGames);
    }

    @GetMapping("/user/{userId}/game/{gameId}")
    public ResponseEntity<OwnedGame> getOwnedGameByUserIdAndGameId(
            @PathVariable String userId,
            @PathVariable String gameId) {
        OwnedGame ownedGame = ownedGameService.getOwnedGameByUserIdAndGameId(userId, gameId);
        if (ownedGame != null) {
            return ResponseEntity.ok(ownedGame);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/acquired-after")
    public ResponseEntity<List<OwnedGame>> getOwnedGamesAcquiredAfter(@RequestParam LocalDateTime date) {
        List<OwnedGame> ownedGames = ownedGameService.getOwnedGamesAcquiredAfter(date);
        return ResponseEntity.ok(ownedGames);
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> countOwnedGamesByUserId(@PathVariable String userId) {
        long count = ownedGameService.countOwnedGamesByUserId(userId);
        return ResponseEntity.ok(count);
    }

    @PostMapping
    public ResponseEntity<OwnedGame> createOwnedGame(@RequestBody OwnedGame ownedGame) {
        OwnedGame createdOwnedGame = ownedGameService.createOwnedGame(ownedGame);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOwnedGame);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OwnedGame> updateOwnedGame(
            @PathVariable String id,
            @RequestBody OwnedGame ownedGameDetails) {
        OwnedGame updatedOwnedGame = ownedGameService.updateOwnedGame(id, ownedGameDetails);
        if (updatedOwnedGame != null) {
            return ResponseEntity.ok(updatedOwnedGame);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwnedGame(@PathVariable String id) {
        ownedGameService.deleteOwnedGame(id);
        return ResponseEntity.noContent().build();
    }
}