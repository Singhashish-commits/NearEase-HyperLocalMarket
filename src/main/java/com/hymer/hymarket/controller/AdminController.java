package com.hymer.hymarket.controller;
import com.hymer.hymarket.dto.ApiResponse;
import com.hymer.hymarket.dto.ProviderPortfolioDto;
import com.hymer.hymarket.dto.ProviderProfileDto;
import com.hymer.hymarket.service.AdminService;
import com.hymer.hymarket.service.ProviderProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final   AdminService adminService;
    private  final ProviderProfileService providerProfileService;
    @Autowired
    public AdminController(AdminService adminService, ProviderProfileService providerProfileService){
        this.adminService = adminService;
        this.providerProfileService = providerProfileService;
    }
    @PostMapping("/provider/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> approve(@PathVariable long id){

        return adminService.approveProviderRequest(id);
    }
    @GetMapping("/provider/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProviderProfileDto>> getPendingAccount(){
        return  ResponseEntity.ok(adminService.pendingRequest());
    }




}
