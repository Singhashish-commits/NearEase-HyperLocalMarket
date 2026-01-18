package com.hymer.hymarket.service;

import com.hymer.hymarket.Repository.ProviderProfileRepository;
import com.hymer.hymarket.Repository.RoleRepository;
import com.hymer.hymarket.Repository.UserRepository;
import com.hymer.hymarket.dto.ApiResponse;
import com.hymer.hymarket.model.ProviderProfile;
import com.hymer.hymarket.model.Roles;
import com.hymer.hymarket.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AdminService {
    private final  UserRepository userRepo;
    private final  ProviderProfileRepository providerRepo;
    private final  RoleRepository roleRepo;
    @Autowired
    public AdminService(UserRepository userRepo, ProviderProfileRepository providerRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.providerRepo = providerRepo;
        this.roleRepo = roleRepo;
    }

@Transactional
    public ResponseEntity<?> approveProviderRequest(Long id) {
        //find user first
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


}
