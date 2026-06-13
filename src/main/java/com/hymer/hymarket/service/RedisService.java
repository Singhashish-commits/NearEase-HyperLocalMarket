package com.hymer.hymarket.service;

import com.hymer.hymarket.dto.ServiceSearchRequestDto;
import com.hymer.hymarket.dto.ServiceSearchResponseDto;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.jsonwebtoken.lang.Collections.nullSafe;

@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final RedissonClient redissonClient;
    private static final String   SEARCH_CACHE = "searchCache";
    private static final long     SEARCH_TTL   = 10;
    @Autowired
    public RedisService(StringRedisTemplate redisTemplate, PasswordEncoder passwordEncoder, RedissonClient redissonClient) {
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = passwordEncoder;
        this.redissonClient = redissonClient;
    }
    public void saveValue(String key, String value, long expireTime){
        redisTemplate.opsForValue().set(key,value, Duration.ofMinutes(expireTime));

    }
    public String getValue(String key){
        return redisTemplate.opsForValue().get(key);
    }
    public void deleteValue(String key){
        redisTemplate.delete(key);
    }

    public String generateSearchKey(ServiceSearchRequestDto dto) {
        return nullSafe(dto.getCategory())                    + ":" +
                nullSafe(dto.getSearchKeyword()).toLowerCase() + ":" +
                nullSafe(dto.getMinPrice())                    + ":" +
                nullSafe(dto.getMaxPrice())                    + ":" +
                nullSafe(dto.getMinRating())                   + ":" +
                nullSafe(dto.getUserLat())                     + ":" +
                nullSafe(dto.getUserLng())                     + ":" +
                nullSafe(dto.getRadiusKm())                    + ":" +
                nullSafe(dto.getSortBy())                      + ":" +
                nullSafe(dto.getSortDirn());
    }

    public void cacheSearchResults(String key, List<ServiceSearchResponseDto> results) {
        RMapCache<String, List<ServiceSearchResponseDto>> cache =
                redissonClient.getMapCache(SEARCH_CACHE);
        cache.put(key, results, SEARCH_TTL, TimeUnit.MINUTES);
    }

    private String nullSafe(Object val) {
        if (val == null) return "null";
        if (val instanceof Double) return String.format("%.2f", val);
        return val.toString().toLowerCase().trim();
    }

    public List<ServiceSearchResponseDto> getCachedSearch(String key) {
        RMapCache<String, List<ServiceSearchResponseDto>> cache =
                redissonClient.getMapCache(SEARCH_CACHE);
        return cache.get(key);
    }
    public void invalidateSearchCache() {
        redissonClient.getMapCache(SEARCH_CACHE).clear();
    }

}
