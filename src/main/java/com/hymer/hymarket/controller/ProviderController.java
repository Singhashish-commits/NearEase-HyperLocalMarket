package com.hymer.hymarket.controller;

import com.hymer.hymarket.dto.ProviderProfileRequest;
import com.hymer.hymarket.dto.ServiceOfferingRequest;
import com.hymer.hymarket.service.ProviderProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/provider")

public class ProviderController {
    private final ProviderProfileService providerProfileService;
    @Autowired
    public ProviderController(ProviderProfileService providerProfileService) {
        this.providerProfileService = providerProfileService;
    }

    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody ProviderProfileRequest request) {
       return   providerProfileService.apply(request);
    }

    @PostMapping("/addService")
    @PreAuthorize(("hasRole('PROVIDER')"))
    public ResponseEntity<?> addService(@RequestBody ServiceOfferingRequest serviceRequest) {
        return providerProfileService.addService(serviceRequest);



    }


}
