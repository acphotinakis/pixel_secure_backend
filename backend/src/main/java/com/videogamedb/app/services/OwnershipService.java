// OwnershipService.java
package com.videogamedb.app.services;

import com.videogamedb.app.models.OwnedGame;
import com.videogamedb.app.models.User;
import com.videogamedb.app.repositories.OwnedGameRepository;
import com.videogamedb.app.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OwnershipService {
    private final OwnedGameRepository ownedGameRepository;
    private final UserRepository userRepository;

    public OwnershipService(OwnedGameRepository ownedGameRepository, UserRepository userRepository) {
        this.ownedGameRepository = ownedGameRepository;
        this.userRepository = userRepository;
    }

    /**
     * Transfer ownership of a game from one user to another.
     * Both updates must succeed or both fail, so we use @Transactional.
     */
    @Transactional
    public void transferOwnership(String gameId, String fromUserId, String toUserId) {
        // Verify that the "from" user owns the game
        OwnedGame ownedGame = ownedGameRepository.findByUserIdAndGameId(fromUserId, gameId);
        if (ownedGame == null) {
            throw new RuntimeException("Source user does not own this game.");
        }

        // Remove ownership from the "from" user
        ownedGameRepository.delete(ownedGame);

        // Add ownership for the "to" user
        OwnedGame newOwnership = new OwnedGame();
        newOwnership.setUserId(toUserId);
        newOwnership.setGameId(gameId);
        newOwnership.setAcquisitionDate(LocalDateTime.now());
        ownedGameRepository.save(newOwnership);

        // Update user records (audit trail, counters, etc.)
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new RuntimeException("Source user not found."));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found."));

        // Update owned game counts (you would need to add these methods to your User
        // model)
        // fromUser.decrementOwnedCount();
        // toUser.incrementOwnedCount();
        userRepository.save(fromUser);
        userRepository.save(toUser);
    }
}