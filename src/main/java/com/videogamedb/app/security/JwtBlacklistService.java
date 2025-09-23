package com.videogamedb.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtBlacklistService {

    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public void blacklistToken(String token) {
        Date expiration = jwtTokenProvider.getExpirationDateFromToken(token);
        long ttl = expiration.getTime() - System.currentTimeMillis();

        if (ttl > 0) {
            String key = BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(key, "blacklisted", ttl, TimeUnit.MILLISECONDS);
        }
    }

    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return redisTemplate.hasKey(key);
    }

    public void cleanupExpiredTokens() {
        // Redis TTL handles automatic cleanup
    }
}