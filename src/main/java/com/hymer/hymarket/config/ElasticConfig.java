package com.hymer.hymarket.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;

@Configuration
public class ElasticConfig {
    @Value("${spring.elasticsearch.uris}")
    private String elasticUrl;
    @Bean
    ElasticsearchClient elasticsearchClient() {
        HttpHost httpHost = HttpHost.create(elasticUrl);
        RestClient client = RestClient.builder(httpHost).build();
        ElasticsearchTransport transport= new RestClientTransport(client,new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);

    }
}
