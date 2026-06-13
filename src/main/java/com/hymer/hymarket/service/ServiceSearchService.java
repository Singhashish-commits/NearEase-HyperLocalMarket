package com.hymer.hymarket.service;

import com.hymer.hymarket.Mapper.ServiceSearchResponseDtoMapper;
import com.hymer.hymarket.Repository.ServiceSearchRepository;
import com.hymer.hymarket.Specification.ServiceSpecification;
import com.hymer.hymarket.dto.ServiceSearchRequestDto;
import com.hymer.hymarket.dto.ServiceSearchResponseDto;
import com.hymer.hymarket.model.ServiceOffering;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceSearchService {
    private final ServiceSearchRepository serviceSearchRepository;
    private final RedisService redisService;

    @Autowired
    public ServiceSearchService(ServiceSearchRepository serviceSearchRepository, RedisService redisService) {
        this.serviceSearchRepository = serviceSearchRepository;
        this.redisService = redisService;
    }

    public List<ServiceSearchResponseDto> searchService(ServiceSearchRequestDto serviceSearchRequestDto) {

        String cacheKey = redisService.generateSearchKey(serviceSearchRequestDto);
        List<ServiceSearchResponseDto> cached = redisService.getCachedSearch(cacheKey);
        if (cached != null) return cached;


        Specification<ServiceOffering> spec = ServiceSpecification.getSpecs(serviceSearchRequestDto);
        List<ServiceOffering> results = serviceSearchRepository.findAll(spec);
        List<ServiceSearchResponseDto> response = results.stream()
                .map(ServiceSearchResponseDtoMapper::mapDto)
                .collect(Collectors.toList());

        redisService.cacheSearchResults(cacheKey, response);
        return response;
    }



}
