package com.hymer.hymarket.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BloomFilterConfig {
    private final RedissonClient redissonClient;
    @Autowired
    public BloomFilterConfig(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }
    @Bean
    public RBloomFilter<String> usernameBloomFilter() {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("usernameFilter");
         /*
          false probability = 1%
          expected insertions = 10 lakh
         */
        bloomFilter.tryInit(1000000, 0.01);
        return bloomFilter;
    }
}
