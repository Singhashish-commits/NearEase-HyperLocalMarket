package com.hymer.hymarket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    @Autowired
    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    public void saveValue(String key, String value, long expireTime){
        redisTemplate.opsForValue().set(key,value, Duration.ofMinutes(expireTime));

    }
    //get key
    public String getValue(String key){
        return redisTemplate.opsForValue().get(key);

    }
    public void deleteValue(String key){
        redisTemplate.delete(key);

    }


}
