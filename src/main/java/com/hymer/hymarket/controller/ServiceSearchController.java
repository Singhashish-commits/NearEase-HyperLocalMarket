package com.hymer.hymarket.controller;

import com.hymer.hymarket.Specification.ServiceSpecification;
import com.hymer.hymarket.dto.ServiceSearchRequestDto;
import com.hymer.hymarket.dto.ServiceSearchResponseDto;
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
    @Autowired
    public ServiceSearchController(ServiceSpecification serviceSpecification, ServiceSearchService serviceSearchService) {
        this.serviceSpecification = serviceSpecification;
        this.serviceSearchService = serviceSearchService;
    }

    @PostMapping("/search")
    public ResponseEntity<List<ServiceSearchResponseDto>> searchServices(@RequestBody ServiceSearchRequestDto serviceSearchRequestDto) {
        return ResponseEntity.ok(serviceSearchService.searchService(serviceSearchRequestDto));
    }


}
