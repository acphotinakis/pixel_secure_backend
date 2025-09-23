// FollowController.java
package com.videogamedb.app.controllers;

import com.videogamedb.app.models.Follow;
import com.videogamedb.app.services.FollowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/follows")
public class FollowController {
    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @GetMapping
    public ResponseEntity<List<Follow>> getAllFollows() {
        List<Follow> follows = followService.getAllFollows();
        return ResponseEntity.ok(follows);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Follow> getFollowById(@PathVariable String id) {
        Optional<Follow> follow = followService.getFollowById(id);
        return follow.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/follower/{followerId}")
    public ResponseEntity<List<Follow>> getFollowsByFollowerId(@PathVariable String followerId) {
        List<Follow> follows = followService.getFollowsByFollowerId(followerId);
        return ResponseEntity.ok(follows);
    }

    @GetMapping("/followed/{followedId}")
    public ResponseEntity<List<Follow>> getFollowsByFollowedId(@PathVariable String followedId) {
        List<Follow> follows = followService.getFollowsByFollowedId(followedId);
        return ResponseEntity.ok(follows);
    }

    @GetMapping("/follower/{followerId}/followed/{followedId}")
    public ResponseEntity<Follow> getFollowByFollowerAndFollowed(
            @PathVariable String followerId,
            @PathVariable String followedId) {
        Follow follow = followService.getFollowByFollowerAndFollowed(followerId, followedId);
        if (follow != null) {
            return ResponseEntity.ok(follow);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/follower/{followerId}/count")
    public ResponseEntity<Long> countByFollowerId(@PathVariable String followerId) {
        long count = followService.countByFollowerId(followerId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/followed/{followedId}/count")
    public ResponseEntity<Long> countByFollowedId(@PathVariable String followedId) {
        long count = followService.countByFollowedId(followedId);
        return ResponseEntity.ok(count);
    }

    @PostMapping
    public ResponseEntity<Follow> createFollow(@RequestBody Follow follow) {
        Follow createdFollow = followService.createFollow(follow);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFollow);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFollow(@PathVariable String id) {
        followService.deleteFollow(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/follower/{followerId}/followed/{followedId}")
    public ResponseEntity<Void> unfollow(
            @PathVariable String followerId,
            @PathVariable String followedId) {
        followService.unfollow(followerId, followedId);
        return ResponseEntity.noContent().build();
    }
}