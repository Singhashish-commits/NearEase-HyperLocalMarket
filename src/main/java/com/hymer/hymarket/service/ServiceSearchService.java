package com.hymer.hymarket.service;

import com.hymer.hymarket.Mapper.ServiceSearchResponseDtoMapper;
import com.hymer.hymarket.Repository.ServiceSearchRepository;
import com.hymer.hymarket.Specification.ServiceSpecification;
import com.hymer.hymarket.dto.ServiceSearchRequestDto;
import com.hymer.hymarket.dto.ServiceSearchResponseDto;
import com.hymer.hymarket.model.ServiceOffering;
import com.hymer.hymarket.model.ServiceOfferingIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceSearchService {
//    private final ServiceSearchRepository serviceSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final RedisService redisService;

    @Autowired
    public ServiceSearchService( RedisService redisService, ElasticsearchOperations elasticsearchOperations) {
//        this.serviceSearchRepository = serviceSearchRepository;
        this.redisService = redisService;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public List<ServiceSearchResponseDto> searchService(ServiceSearchRequestDto requestDto) {

        String cacheKey = redisService.generateSearchKey(requestDto);
        List<ServiceSearchResponseDto> cached = redisService.getCachedSearch(cacheKey);
        if (cached != null) return cached;


//        Specification<ServiceOffering> spec = ServiceSpecification.getSpecs(serviceSearchRequestDto);
//        List<ServiceOffering> results = serviceSearchRepository.findAll(spec);
//        List<ServiceSearchResponseDto> response = results.stream()
//                .map(ServiceSearchResponseDtoMapper::mapDto)
//                .collect(Collectors.toList());

//        redisService.cacheSearchResults(cacheKey, response);
//        return response;

        Criteria criteria= new Criteria("id").exists();
        if(requestDto.getCategory() != null && !requestDto.getCategory().trim().isEmpty()){
            criteria.and("category").contains(requestDto.getCategory());
        }
        if(requestDto.getSearchKeyword() != null && !requestDto.getSearchKeyword().trim().isEmpty()){
            criteria.and("searchKeyword").contains(requestDto.getSearchKeyword());
        }
        if(requestDto.getMinPrice()!=null){
            criteria.and("price").greaterThanEqual(requestDto.getMinPrice());
        }
        if(requestDto.getMaxPrice()!=null){
            criteria.and("price").lessThanEqual(requestDto.getMaxPrice());
        }
//        if(requestDto.get)
        Query query = new CriteriaQuery(criteria);
        SearchHits<ServiceOfferingIndex> searchHits = elasticsearchOperations.search(query, ServiceOfferingIndex.class);

        List<ServiceSearchResponseDto> response = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(ServiceSearchResponseDtoMapper::mapDto)
                .collect(Collectors.toList());

        redisService.cacheSearchResults(cacheKey, response);
        return response;


    }



}
