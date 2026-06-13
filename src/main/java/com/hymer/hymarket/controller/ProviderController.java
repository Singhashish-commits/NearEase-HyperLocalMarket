package com.hymer.hymarket.controller;

import com.hymer.hymarket.dto.*;
import com.hymer.hymarket.service.ProviderProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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

    @GetMapping("/my-portfolio")
    public ResponseEntity<List<ProviderPortfolioDto>> getMyPortfolio(){
        return ResponseEntity.ok(providerProfileService.getMyPortfolio());
    }

    @DeleteMapping("my-portfolio/{bookingId}/images")
    public ResponseEntity<ApiResponse> removeImages(@PathVariable("bookingId") Long bookingId){
        providerProfileService.removePortfolioImages(bookingId);
        return ResponseEntity.ok(new ApiResponse(true,"Portfolio images removed successfully"));
    }

    @PostMapping(value = "/addService",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize(("hasRole('PROVIDER')"))
    public ResponseEntity<ApiResponse> addService(
            @RequestPart("serviceDetails") ServiceOfferingRequest serviceRequest,
            @RequestPart(value ="file",required = false) MultipartFile file
            ) throws Exception {
        return providerProfileService.addService(serviceRequest,file);

    }

    @GetMapping("my/DashBoard")
    public ResponseEntity<ProviderDashBoardDto>  getDashBoard(){
        ProviderDashBoardDto providerDashBoardDto = providerProfileService.getProviderDashBoard();
        return ResponseEntity.ok(providerDashBoardDto);
    }

    @PostMapping("/my-services")
    public ResponseEntity<List<ServiceOfferingResponse>> getMyServices(){
        return ResponseEntity.ok(providerProfileService.MyServices());

    }

    @PostMapping(value = "/edit-service/{id}",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse> editService(
            @PathVariable Long id , @RequestPart("serviceDetails") ServiceOfferingRequest serviceRequest,
            @RequestPart(value ="file",required = false) MultipartFile file) throws IOException {
                return ResponseEntity.ok(providerProfileService.editService(id,serviceRequest,file));
    }



}
