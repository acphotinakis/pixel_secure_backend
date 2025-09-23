package com.videogamedb.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitService {

    private static final int MAX_ATTEMPTS_PER_HOUR = 5;
    private static final int MAX_REQUESTS_PER_MINUTE = 100;

    @Autowired
    private CacheManager cacheManager;

    private final ConcurrentHashMap<String, AtomicInteger> loginAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();

    public boolean isRateLimited(String identifier, RateLimitType type) {
        String key = type + ":" + identifier;

        if (type == RateLimitType.LOGIN) {
            AtomicInteger attempts = loginAttempts.computeIfAbsent(key, k -> new AtomicInteger(0));
            return attempts.incrementAndGet() > MAX_ATTEMPTS_PER_HOUR;
        } else if (type == RateLimitType.REQUEST) {
            AtomicInteger requests = requestCounts.computeIfAbsent(key, k -> new AtomicInteger(0));
            return requests.incrementAndGet() > MAX_REQUESTS_PER_MINUTE;
        }
        return false;
    }

    public void resetAttempts(String identifier, RateLimitType type) {
        String key = type + ":" + identifier;
        if (type == RateLimitType.LOGIN) {
            loginAttempts.remove(key);
        } else if (type == RateLimitType.REQUEST) {
            requestCounts.remove(key);
        }
    }

    public int getRemainingAttempts(String identifier, RateLimitType type) {
        String key = type + ":" + identifier;
        if (type == RateLimitType.LOGIN) {
            AtomicInteger attempts = loginAttempts.get(key);
            return attempts != null ? MAX_ATTEMPTS_PER_HOUR - attempts.get() : MAX_ATTEMPTS_PER_HOUR;
        }
        return 0;
    }

    public enum RateLimitType {
        LOGIN, REQUEST
    }
}