// OwnershipController.java
package com.videogamedb.app.controllers;

import com.videogamedb.app.services.OwnershipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ownership")
public class OwnershipController {
    private final OwnershipService ownershipService;

    public OwnershipController(OwnershipService ownershipService) {
        this.ownershipService = ownershipService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferOwnership(
            @RequestParam String gameId,
            @RequestParam String fromUserId,
            @RequestParam String toUserId) {
        try {
            ownershipService.transferOwnership(gameId, fromUserId, toUserId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}