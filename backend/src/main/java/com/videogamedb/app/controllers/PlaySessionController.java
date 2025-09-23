// PlaySessionController.java
package com.videogamedb.app.controllers;

import com.videogamedb.app.models.PlaySession;
import com.videogamedb.app.services.PlaySessionService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/play-sessions")
public class PlaySessionController {
    private final PlaySessionService playSessionService;

    public PlaySessionController(PlaySessionService playSessionService) {
        this.playSessionService = playSessionService;
    }

    @GetMapping
    public ResponseEntity<List<PlaySession>> getAllPlaySessions() {
        List<PlaySession> playSessions = playSessionService.getAllPlaySessions();
        return ResponseEntity.ok(playSessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaySession> getPlaySessionById(@PathVariable String id) {
        Optional<PlaySession> playSession = playSessionService.getPlaySessionById(id);
        return playSession.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PlaySession>> getPlaySessionsByUserId(@PathVariable String userId) {
        List<PlaySession> playSessions = playSessionService.getPlaySessionsByUserId(userId);
        return ResponseEntity.ok(playSessions);
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<PlaySession>> getPlaySessionsByGameId(@PathVariable String gameId) {
        List<PlaySession> playSessions = playSessionService.getPlaySessionsByGameId(gameId);
        return ResponseEntity.ok(playSessions);
    }

    @GetMapping("/user/{userId}/game/{gameId}")
    public ResponseEntity<List<PlaySession>> getPlaySessionsByUserIdAndGameId(
            @PathVariable String userId,
            @PathVariable String gameId) {
        List<PlaySession> playSessions = playSessionService.getPlaySessionsByUserIdAndGameId(userId, gameId);
        return ResponseEntity.ok(playSessions);
    }

    @GetMapping("/after")
    public ResponseEntity<List<PlaySession>> getPlaySessionsAfter(@RequestParam LocalDateTime date) {
        List<PlaySession> playSessions = playSessionService.getPlaySessionsAfter(date);
        return ResponseEntity.ok(playSessions);
    }

    @GetMapping("/range")
    public ResponseEntity<List<PlaySession>> getPlaySessionsBetween(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        List<PlaySession> playSessions = playSessionService.getPlaySessionsBetween(start, end);
        return ResponseEntity.ok(playSessions);
    }

    @GetMapping("/user/{userId}/game/{gameId}/count")
    public ResponseEntity<Long> countPlaySessionsByUserIdAndGameId(
            @PathVariable String userId,
            @PathVariable String gameId) {
        long count = playSessionService.countPlaySessionsByUserIdAndGameId(userId, gameId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<List<PlaySession>> getLatestPlaySessionsByUserId(
            @PathVariable String userId,
            Pageable pageable) {
        List<PlaySession> playSessions = playSessionService.getLatestPlaySessionsByUserId(userId, pageable);
        return ResponseEntity.ok(playSessions);
    }

    @PostMapping
    public ResponseEntity<PlaySession> createPlaySession(@RequestBody PlaySession playSession) {
        PlaySession createdPlaySession = playSessionService.createPlaySession(playSession);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlaySession);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaySession> updatePlaySession(
            @PathVariable String id,
            @RequestBody PlaySession playSessionDetails) {
        PlaySession updatedPlaySession = playSessionService.updatePlaySession(id, playSessionDetails);
        if (updatedPlaySession != null) {
            return ResponseEntity.ok(updatedPlaySession);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaySession(@PathVariable String id) {
        playSessionService.deletePlaySession(id);
        return ResponseEntity.noContent().build();
    }
}