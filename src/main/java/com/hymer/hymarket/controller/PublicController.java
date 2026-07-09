package com.hymer.hymarket.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import co.elastic.clients.transport.ElasticsearchTransport;
import com.hymer.hymarket.dto.ProviderPortfolioDto;
import com.hymer.hymarket.dto.ServiceOfferingResponse;
import com.hymer.hymarket.model.ServiceCategory;
import com.hymer.hymarket.model.ServiceType;
import com.hymer.hymarket.service.ProviderProfileService;
import com.hymer.hymarket.service.PublicService;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@CrossOrigin
@RequestMapping("/api/public")
public class PublicController {
    private PublicService publicService;
    private ProviderProfileService providerProfileService;
    ElasticsearchClient elasticSearchClient;
    @Autowired
    public PublicController(PublicService publicService, ProviderProfileService providerProfileService, ElasticsearchClient elasticSearchClient) {
        this.publicService = publicService;
        this.providerProfileService = providerProfileService;
        this.elasticSearchClient = elasticSearchClient;
    }

    @Autowired
    public void setPublicService(PublicService publicService) {
        this.publicService = publicService;
    }
    @GetMapping("/categories/")
    public List<ServiceCategory>  getServiceCategories(){
        return publicService.getAllCategory();

    }

    @GetMapping("/categories/{categoryName}/types")
    public List<ServiceType> getServiceTypes(@PathVariable String categoryName){
        return publicService.getAllServiceType(categoryName);

    }
    @GetMapping("/type/{typeId}/offering")
    public List<ServiceOfferingResponse> getServiceOffering(@PathVariable Long typeId){
        return publicService.getOfferingByTypeId(typeId);
    }
    @GetMapping("/providers/{providerId}/portfolio")
    public ResponseEntity<List<ProviderPortfolioDto>> getProviderPortfolio(@PathVariable Long providerId){
        return ResponseEntity.ok(providerProfileService.getProviderPortfolio(providerId));
    }
    @GetMapping("services/all")
    public ResponseEntity<List<ServiceOfferingResponse>>  getAllServiceOffering(){
        return ResponseEntity.ok(publicService.getAllServices());
    }




    @GetMapping("/")
    public String health() {
        return "NearEase Backend is Running ";
    }

    @GetMapping("/elastic-test")
    public String test() throws IOException {

        try {
            InfoResponse info = elasticSearchClient.info();
            return info.version().number();
        } catch (Exception e) {
            e.printStackTrace();
            return "Connection Failed";
        }
    }





}
