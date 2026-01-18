package com.hymer.hymarket.service;

import com.hymer.hymarket.Repository.ProviderProfileRepository;
import com.hymer.hymarket.Repository.ServiceOfferingRepo;
import com.hymer.hymarket.Repository.ServiceTypeRepo;
import com.hymer.hymarket.Repository.UserRepository;
import com.hymer.hymarket.dto.ApiResponse;
import com.hymer.hymarket.dto.ProviderProfileRequest;
import com.hymer.hymarket.dto.ServiceOfferingRequest;
import com.hymer.hymarket.model.ProviderProfile;
import com.hymer.hymarket.model.ServiceOffering;
import com.hymer.hymarket.model.ServiceType;
import com.hymer.hymarket.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
public class ProviderProfileService {
    private final ServiceTypeRepo serviceTypeRepo;
    private final ServiceOfferingRepo serviceOfferingRepo;
    private final UserRepository userRepository;
    private  final ProviderProfileRepository providerRepo;

    @Autowired
    public ProviderProfileService(UserRepository userRepository, ProviderProfileRepository providerRepo, ServiceTypeRepo serviceTypeRepo, ServiceOfferingRepo serviceOfferingRepo) {
        this.userRepository = userRepository;
        this.providerRepo = providerRepo;
        this.serviceTypeRepo = serviceTypeRepo;
        this.serviceOfferingRepo = serviceOfferingRepo;
    }

    // apply to be Provider
    public ResponseEntity<?> apply(@RequestBody ProviderProfileRequest request){
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        String email =  userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));

        // Already Applied
        if(user.getProviderProfile()!= null){
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Provider Profile already exists"));
        }
        ProviderProfile providerProfile = new ProviderProfile();
        providerProfile.setUser(user);
        providerProfile.setBio(request.getBio());
        providerProfile.setSkills(request.getSkills());
        providerProfile.setExperience(request.getExperience());
        providerProfile.setVerified(false);
        providerProfile.setAddress(request.getAddress());
        providerRepo.save(providerProfile);
                return ResponseEntity.ok(new ApiResponse(true, "Application Submitted for verification"));

    }

    public ResponseEntity<?> addService(ServiceOfferingRequest serviceRequest) {
        //gets the currently logged-in user
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        User user =  userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()-> new RuntimeException("User not found"));
         // get their provider profile
        ProviderProfile providerProfile  = user.getProviderProfile();
         if(providerProfile==null){
             return  ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Provider Profile not found"));
         }
         if(!providerProfile.isVerified()){
             return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Provider Profile is not verified"));
         }
       ServiceType serviceType = serviceTypeRepo.findById(serviceRequest.getServiceTypeId())
                .orElseThrow(()-> new RuntimeException("Service Type not find with ID "+serviceRequest.getServiceTypeId()));

         ServiceOffering serviceOffering = new ServiceOffering();
         serviceOffering.setProviderProfile(providerProfile);
         serviceOffering.setServiceType(serviceType);
         serviceOffering.setPrice(serviceRequest.getPrice());
         serviceOffering.setDescription(serviceRequest.getDescription());
         serviceOfferingRepo.save(serviceOffering);

         return ResponseEntity.ok(new ApiResponse(true, "Service Added Successfully"));

    }
}
