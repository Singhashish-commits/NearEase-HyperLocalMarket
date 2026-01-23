package com.hymer.hymarket.controller;
import com.hymer.hymarket.service.AdminService;
import com.hymer.hymarket.service.ProviderProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> approve(@PathVariable long id){

        return adminService.approveProviderRequest(id);
    }


}
