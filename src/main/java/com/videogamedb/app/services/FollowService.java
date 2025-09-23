// FollowService.java
package com.videogamedb.app.services;

import com.videogamedb.app.models.Follow;
import com.videogamedb.app.repositories.FollowRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FollowService {
    private final FollowRepository followRepository;

    public FollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public List<Follow> getAllFollows() {
        return followRepository.findAll();
    }

    public Optional<Follow> getFollowById(String id) {
        return followRepository.findById(id);
    }

    public List<Follow> getFollowsByFollowerId(String followerId) {
        return followRepository.findByFollowerId(followerId);
    }

    public List<Follow> getFollowsByFollowedId(String followedId) {
        return followRepository.findByFollowedId(followedId);
    }

    public Follow getFollowByFollowerAndFollowed(String followerId, String followedId) {
        return followRepository.findByFollowerIdAndFollowedId(followerId, followedId);
    }

    public long countByFollowerId(String followerId) {
        return followRepository.countByFollowerId(followerId);
    }

    public long countByFollowedId(String followedId) {
        return followRepository.countByFollowedId(followedId);
    }

    public Follow createFollow(Follow follow) {
        return followRepository.save(follow);
    }

    public void deleteFollow(String id) {
        followRepository.deleteById(id);
    }

    public void unfollow(String followerId, String followedId) {
        Follow follow = followRepository.findByFollowerIdAndFollowedId(followerId, followedId);
        if (follow != null) {
            followRepository.delete(follow);
        }
    }
}