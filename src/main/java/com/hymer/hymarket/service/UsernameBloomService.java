package com.hymer.hymarket.service;

import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsernameBloomService {
    private final RBloomFilter<String> usernameBloomFilter;
    @Autowired
    public UsernameBloomService(RBloomFilter<String> usernameBloomFilter) {
        this.usernameBloomFilter = usernameBloomFilter;
    }
    public void addUsername(String username) {
        usernameBloomFilter.add(username.toLowerCase());
    }

    public boolean mightExist(String username) {
        return usernameBloomFilter.contains(username.toLowerCase());
    }
}
