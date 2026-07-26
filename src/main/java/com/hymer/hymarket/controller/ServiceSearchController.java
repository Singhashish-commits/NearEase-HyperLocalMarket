package com.hymer.hymarket.controller;

import com.hymer.hymarket.Specification.ServiceSpecification;
import com.hymer.hymarket.dto.ServiceSearchRequestDto;
import com.hymer.hymarket.dto.ServiceSearchResponseDto;
import com.hymer.hymarket.service.RedisService;
import com.hymer.hymarket.service.ServiceSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@RequestMapping("/api/services")
public class ServiceSearchController {
  private final  ServiceSpecification serviceSpecification;
  private final ServiceSearchService serviceSearchService;
  private final RedisService redisService;
    @Autowired
    public ServiceSearchController(ServiceSpecification serviceSpecification, ServiceSearchService serviceSearchService, RedisService redisService) {
        this.serviceSpecification = serviceSpecification;
        this.serviceSearchService = serviceSearchService;
        this.redisService = redisService;
    }

    @PostMapping("/search")
    public ResponseEntity<List<ServiceSearchResponseDto>> searchServices(@RequestBody ServiceSearchRequestDto serviceSearchRequestDto) {
        return ResponseEntity.ok(serviceSearchService.searchService(serviceSearchRequestDto));
    }
    @PostMapping("/clear-cache")
    public ResponseEntity<String> clearCache() {
        redisService.invalidateSearchCache();
        return ResponseEntity.ok("Cache cleared");
    }

}
