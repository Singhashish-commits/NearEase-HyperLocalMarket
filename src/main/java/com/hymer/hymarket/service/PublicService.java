package com.hymer.hymarket.service;

import com.hymer.hymarket.Repository.ServiceCategoryRepo;
import com.hymer.hymarket.Repository.ServiceOfferingRepo;
import com.hymer.hymarket.Repository.ServiceTypeRepo;
import com.hymer.hymarket.dto.ProviderProfileDto;
import com.hymer.hymarket.dto.ServiceOfferingResponse;
import com.hymer.hymarket.dto.UserProfileDto;
import com.hymer.hymarket.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PublicService {
    private final ServiceCategoryRepo serviceCategoryRepo;
    private final ServiceTypeRepo serviceTypeRepo;
    private final ServiceOfferingRepo serviceOfferingRepo;

    @Autowired
    public PublicService(ServiceTypeRepo serviceTypeRepo,ServiceCategoryRepo serviceCategoryRepo,ServiceOfferingRepo serviceOfferingRepo){
        this.serviceTypeRepo = serviceTypeRepo;
        this.serviceCategoryRepo = serviceCategoryRepo;
        this.serviceOfferingRepo = serviceOfferingRepo;
    }

    // gets the category that is SemiLux and the Lux
    public List<ServiceCategory> getAllCategory(){
        return serviceCategoryRepo.findAll();
    }
    // 2. Get Sub-Menu (e.g., User clicks "Luxurious" -> Returns ["Spas", "Fine Dining"])
    public List<ServiceType> getAllServiceType(String categoryName){
        ServiceCategory category = serviceCategoryRepo.findByName(categoryName)
                .orElseThrow(()->  new RuntimeException("Category not Found"));
        return serviceTypeRepo.findByCategoryId(category.getId());

    }

    // 3. Get The List (e.g., User clicks "Spas" -> Returns specific providers)
    public List<ServiceOfferingResponse> getOfferingByTypeId(Long TypeId){
        List<ServiceOffering> offering = serviceOfferingRepo.findByServiceTypeId(TypeId);
        return offering.stream().map(this :: mapDto).collect(Collectors.toList());

    }

    private ServiceOfferingResponse mapDto(ServiceOffering offering) {
        //Mapping the user Profile of the provider
        User user = offering.getProviderProfile().getUser();
        UserProfileDto userDto = new UserProfileDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setPhone(user.getPhoneNumber());
        // Mapping the Provider Profile of the offering that is being provided by the provider
        ProviderProfile profile = offering.getProviderProfile();
        ProviderProfileDto providerDto = new ProviderProfileDto();
        providerDto.setId(profile.getId());
        providerDto.setBio(profile.getBio());
        providerDto.setAddress(profile.getAddress());
        providerDto.setVerified(profile.isVerified());
        providerDto.setSkill(profile.getSkills());
        providerDto.setExperience(profile.getExperience());
        providerDto.setUser(userDto);

        ServiceOfferingResponse serviceOfferingResponse = new ServiceOfferingResponse();
        serviceOfferingResponse.setId(offering.getId());
        serviceOfferingResponse.setPrice(offering.getPrice());
        serviceOfferingResponse.setDescription(offering.getDescription());
        serviceOfferingResponse.setServiceTypename(offering.getServiceType().getName());
        serviceOfferingResponse.setProvider(providerDto);

        return serviceOfferingResponse;

    }


}
