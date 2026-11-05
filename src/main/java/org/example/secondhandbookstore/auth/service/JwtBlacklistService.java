package org.example.secondhandbookstore.auth.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {
    public static final String JWT_BLACKLIST_JTI_PREFIX = "jwt:blacklist:";

    private final StringRedisTemplate stringRedisTemplate;

    public void addToBlacklist(String jti , Duration ttl){
        stringRedisTemplate.opsForValue().set(JWT_BLACKLIST_JTI_PREFIX+jti,"1",ttl);
    }

    public boolean isBlacklisted(String jti){
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(JWT_BLACKLIST_JTI_PREFIX + jti));
    }

}
