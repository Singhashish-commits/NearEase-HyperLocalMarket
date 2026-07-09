package com.hymer.hymarket.service;

import com.hymer.hymarket.Mapper.ProviderProfileDtoMapper;
import com.hymer.hymarket.Repository.*;
import com.hymer.hymarket.dto.ApiResponse;
import com.hymer.hymarket.dto.ProviderPortfolioDto;
import com.hymer.hymarket.dto.ProviderProfileDto;
import com.hymer.hymarket.model.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class AdminService {
    private final  UserRepository userRepo;
    private final  ProviderProfileRepository providerRepo;
    private final  RoleRepository roleRepo;
    private final ServiceOfferingRepo serviceOfferingRepo;
    private final ServiceSearchRepository serviceSearchRepo;

    @Autowired
    public AdminService(UserRepository userRepo, ProviderProfileRepository providerRepo, RoleRepository roleRepo, ServiceOfferingRepo serviceOfferingRepo, ServiceSearchRepository serviceSearchRepo) {
        this.userRepo = userRepo;
        this.providerRepo = providerRepo;
        this.roleRepo = roleRepo;
        this.serviceOfferingRepo = serviceOfferingRepo;
        this.serviceSearchRepo = serviceSearchRepo;
    }

@Transactional
    public ResponseEntity<ApiResponse> approveProviderRequest(Long id) {
        User user = userRepo.findById(id).orElseThrow(()->new EntityNotFoundException("User not found"));
        ProviderProfile profile = user.getProviderProfile();
        if(profile==null){
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false,"This User haven't applied for the Provider Profile"));
        }
        profile.setVerified(true);
        providerRepo.save(profile);
        Roles role = roleRepo.findByName("ROLE_PROVIDER").orElseThrow(()-> new RuntimeException("Role not found"));
        Set<Roles> roles =  user.getRoles();
        roles.add(role);
        user.setRoles(roles);
        userRepo.save(user);
        return ResponseEntity.ok().body(new ApiResponse(true,"Provider has been approved"));

    }


    public List<ProviderProfileDto> pendingRequest() {
       String email = SecurityContextHolder.getContext().getAuthentication().getName();
       User user  = userRepo.findByEmail(email)
               .orElseThrow(()->new EntityNotFoundException("User not found"));
      Set<Roles> roles = user.getRoles();

        boolean isAdmin = roles.stream()
                .anyMatch(role ->
                        role.getName().equals("ROLE_ADMIN"));

      List<ProviderProfile> providerProfile = providerRepo.findByIsVerified(false);
      return providerProfile.stream().map(ProviderProfileDtoMapper::mapDto).toList();

    }

    public ApiResponse migrateData() {
        List<ServiceOffering> allService = serviceOfferingRepo.findAll();
        List<ServiceOfferingIndex> indexList = allService.stream().map(offering -> {
            ServiceOfferingIndex index = new ServiceOfferingIndex();
            index.setId(String.valueOf(offering.getId()));
            index.setDescription(offering.getDescription());
            index.setPrice(offering.getPrice());
            index.setServiceTitle(offering.getServiceTitle());
            if (offering.getServiceType() != null) {
//                index.setCategoryName(offering.getServiceType().getName());
                index.setCategory(offering.getServiceType().getName());
            }
            return index;
        }).toList();

        serviceSearchRepo.saveAll(indexList);
        return new ApiResponse(true,"Successfully migrated " + indexList.size() + " services to Elasticsearch!");
    }
}
