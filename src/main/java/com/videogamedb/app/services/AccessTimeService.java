// AccessTimeService.java
package com.videogamedb.app.services;

import com.videogamedb.app.models.AccessTime;
import com.videogamedb.app.repositories.AccessTimeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccessTimeService {
    private final AccessTimeRepository accessTimeRepository;

    public AccessTimeService(AccessTimeRepository accessTimeRepository) {
        this.accessTimeRepository = accessTimeRepository;
    }

    public List<AccessTime> getAllAccessTimes() {
        return accessTimeRepository.findAll();
    }

    public Optional<AccessTime> getAccessTimeById(String id) {
        return accessTimeRepository.findById(id);
    }

    public List<AccessTime> getAccessTimesByUserId(String userId) {
        return accessTimeRepository.findByUserId(userId);
    }

    public List<AccessTime> getAccessTimesAfter(LocalDateTime time) {
        return accessTimeRepository.findByTimeAfter(time);
    }

    public List<AccessTime> getAccessTimesBetween(LocalDateTime start, LocalDateTime end) {
        return accessTimeRepository.findByTimeBetween(start, end);
    }

    public List<AccessTime> getUserAccessInDateRange(String userId, LocalDateTime start, LocalDateTime end) {
        return accessTimeRepository.findUserAccessInDateRange(userId, start, end);
    }

    public AccessTime createAccessTime(AccessTime accessTime) {
        return accessTimeRepository.save(accessTime);
    }

    public AccessTime updateAccessTime(String id, AccessTime accessTimeDetails) {
        Optional<AccessTime> optionalAccessTime = accessTimeRepository.findById(id);
        if (optionalAccessTime.isPresent()) {
            AccessTime accessTime = optionalAccessTime.get();
            accessTime.setUserId(accessTimeDetails.getUserId());
            accessTime.setTime(accessTimeDetails.getTime());
            return accessTimeRepository.save(accessTime);
        }
        return null;
    }

    public void deleteAccessTime(String id) {
        accessTimeRepository.deleteById(id);
    }
}