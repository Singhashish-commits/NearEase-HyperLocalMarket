package com.hymer.hymarket;

import com.hymer.hymarket.Repository.ServiceSearchRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestElasticsearchConfig {
    @Bean
    @Primary
    public ServiceSearchRepository serviceSearchRepository() {
        return mock(ServiceSearchRepository.class);
    }
    @Bean
    @Primary
    public ElasticsearchOperations elasticsearchOperations() {
        return mock(ElasticsearchOperations.class);
    }


}
