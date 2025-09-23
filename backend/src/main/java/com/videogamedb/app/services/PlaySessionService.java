// PlaySessionService.java
package com.videogamedb.app.services;

import com.videogamedb.app.models.PlaySession;
import com.videogamedb.app.repositories.PlaySessionRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PlaySessionService {
    private final PlaySessionRepository playSessionRepository;

    public PlaySessionService(PlaySessionRepository playSessionRepository) {
        this.playSessionRepository = playSessionRepository;
    }

    public List<PlaySession> getAllPlaySessions() {
        return playSessionRepository.findAll();
    }

    public Optional<PlaySession> getPlaySessionById(String id) {
        return playSessionRepository.findById(id);
    }

    public List<PlaySession> getPlaySessionsByUserId(String userId) {
        return playSessionRepository.findByUserId(userId);
    }

    public List<PlaySession> getPlaySessionsByGameId(String gameId) {
        return playSessionRepository.findByGameId(gameId);
    }

    public List<PlaySession> getPlaySessionsByUserIdAndGameId(String userId, String gameId) {
        return playSessionRepository.findByUserIdAndGameId(userId, gameId);
    }

    public List<PlaySession> getPlaySessionsAfter(LocalDateTime date) {
        return playSessionRepository.findByDatetimeOpenedAfter(date);
    }

    public List<PlaySession> getPlaySessionsBetween(LocalDateTime start, LocalDateTime end) {
        return playSessionRepository.findByDatetimeOpenedBetween(start, end);
    }

    public long countPlaySessionsByUserIdAndGameId(String userId, String gameId) {
        return playSessionRepository.countByUserIdAndGameId(userId, gameId);
    }

    public List<PlaySession> getLatestPlaySessionsByUserId(String userId, Pageable pageable) {
        return playSessionRepository.findLatestByUserId(userId, pageable);
    }

    public PlaySession createPlaySession(PlaySession playSession) {
        return playSessionRepository.save(playSession);
    }

    public PlaySession updatePlaySession(String id, PlaySession playSessionDetails) {
        Optional<PlaySession> optionalPlaySession = playSessionRepository.findById(id);
        if (optionalPlaySession.isPresent()) {
            PlaySession playSession = optionalPlaySession.get();
            playSession.setUserId(playSessionDetails.getUserId());
            playSession.setGameId(playSessionDetails.getGameId());
            playSession.setDatetimeOpened(playSessionDetails.getDatetimeOpened());
            playSession.setTimePlayed(playSessionDetails.getTimePlayed());
            return playSessionRepository.save(playSession);
        }
        return null;
    }

    public void deletePlaySession(String id) {
        playSessionRepository.deleteById(id);
    }
}