package com.hymer.hymarket.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.json.JsonData;
import com.hymer.hymarket.Mapper.ServiceSearchResponseDtoMapper;
import com.hymer.hymarket.Repository.ServiceSearchRepository;
import com.hymer.hymarket.Specification.ServiceSpecification;
import com.hymer.hymarket.dto.ServiceSearchRequestDto;
import com.hymer.hymarket.dto.ServiceSearchResponseDto;
import com.hymer.hymarket.model.ServiceOffering;
import com.hymer.hymarket.model.ServiceOfferingIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

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

        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
        boolBuilder.must(m -> m.exists(e -> e.field("id")));
        if(requestDto.getCategory()!=null && !requestDto.getCategory().trim().isEmpty()){
            boolBuilder.must(m -> m.term(t -> t
                    .field("category")
                    .value(requestDto.getCategory())
            ));
        }
        if (requestDto.getSearchKeyword() != null && !requestDto.getSearchKeyword().trim().isEmpty()) {
            String kw = requestDto.getSearchKeyword();
            boolBuilder.must(m -> m.bool(b -> b
                    .should(s -> s.match(mt -> mt.field("serviceTitle").query(kw)))
                    .should(s -> s.match(mt -> mt.field("serviceName").query(kw)))
                    .should(s -> s.match(mt -> mt.field("description").query(kw)))
                    .minimumShouldMatch("1")
            ));
        }
        if (requestDto.getMinPrice() != null) {
            boolBuilder.must(m -> m.range(r -> r
                    .number(n -> n
                            .field("price")
                            .gte(requestDto.getMinPrice())
                    )
            ));
        }
        if (requestDto.getMaxPrice() != null) {
            boolBuilder.must(m -> m.range(r -> r
                    .number(n -> n
                            .field("price")
                            .lte(requestDto.getMaxPrice())
                    )
            ));
        }
        if (requestDto.getMaxPrice() != null) {
            boolBuilder.must(m -> m.range(r -> r
                    .number(n -> n
                            .field("price")
                            .lte(requestDto.getMaxPrice())
                    )
            ));
        }
        Query esQuery = Query.of(q -> q.bool(boolBuilder.build()));

        // 2. Initialize the NativeQuery builder (Use 'var' or 'NativeQueryBuilder')
        NativeQueryBuilder queryBuilder = NativeQuery.builder()
                .withQuery(esQuery);

        // 3. Add Sorting if requested
        if (requestDto.getSortBy() != null && !requestDto.getSortBy().trim().isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(requestDto.getSortDirn())
                    ? Sort.Direction.DESC : Sort.Direction.ASC;

            queryBuilder.withSort(Sort.by(direction, requestDto.getSortBy()));
        }

        // 4. Build the final NativeQuery
        NativeQuery query = queryBuilder.build();

        SearchHits<ServiceOfferingIndex> searchHits =
                elasticsearchOperations.search(query, ServiceOfferingIndex.class);

        List<ServiceSearchResponseDto> response = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(ServiceSearchResponseDtoMapper::mapDto)
                .collect(Collectors.toList());

        redisService.cacheSearchResults(cacheKey, response);
        return response;

    }



}
