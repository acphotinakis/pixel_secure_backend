// AccessTimeController.java
package com.videogamedb.app.controllers;

import com.videogamedb.app.models.AccessTime;
import com.videogamedb.app.services.AccessTimeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/access-times")
public class AccessTimeController {
    private final AccessTimeService accessTimeService;

    public AccessTimeController(AccessTimeService accessTimeService) {
        this.accessTimeService = accessTimeService;
    }

    @GetMapping
    public ResponseEntity<List<AccessTime>> getAllAccessTimes() {
        List<AccessTime> accessTimes = accessTimeService.getAllAccessTimes();
        return ResponseEntity.ok(accessTimes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccessTime> getAccessTimeById(@PathVariable String id) {
        Optional<AccessTime> accessTime = accessTimeService.getAccessTimeById(id);
        return accessTime.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccessTime>> getAccessTimesByUserId(@PathVariable String userId) {
        List<AccessTime> accessTimes = accessTimeService.getAccessTimesByUserId(userId);
        return ResponseEntity.ok(accessTimes);
    }

    @GetMapping("/after")
    public ResponseEntity<List<AccessTime>> getAccessTimesAfter(@RequestParam LocalDateTime time) {
        List<AccessTime> accessTimes = accessTimeService.getAccessTimesAfter(time);
        return ResponseEntity.ok(accessTimes);
    }

    @GetMapping("/range")
    public ResponseEntity<List<AccessTime>> getAccessTimesBetween(
            @RequestParam LocalDateTime start, 
            @RequestParam LocalDateTime end) {
        List<AccessTime> accessTimes = accessTimeService.getAccessTimesBetween(start, end);
        return ResponseEntity.ok(accessTimes);
    }

    @GetMapping("/user/{userId}/range")
    public ResponseEntity<List<AccessTime>> getUserAccessInDateRange(
            @PathVariable String userId,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        List<AccessTime> accessTimes = accessTimeService.getUserAccessInDateRange(userId, start, end);
        return ResponseEntity.ok(accessTimes);
    }

    @PostMapping
    public ResponseEntity<AccessTime> createAccessTime(@RequestBody AccessTime accessTime) {
        AccessTime createdAccessTime = accessTimeService.createAccessTime(accessTime);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccessTime);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccessTime> updateAccessTime(
            @PathVariable String id, 
            @RequestBody AccessTime accessTimeDetails) {
        AccessTime updatedAccessTime = accessTimeService.updateAccessTime(id, accessTimeDetails);
        if (updatedAccessTime != null) {
            return ResponseEntity.ok(updatedAccessTime);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccessTime(@PathVariable String id) {
        accessTimeService.deleteAccessTime(id);
        return ResponseEntity.noContent().build();
    }
}