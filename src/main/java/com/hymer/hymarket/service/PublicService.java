package com.hymer.hymarket.service;

import com.hymer.hymarket.Mapper.ServiceOfferingMapper;
import com.hymer.hymarket.Repository.ServiceCategoryRepo;
import com.hymer.hymarket.Repository.ServiceOfferingRepo;
import com.hymer.hymarket.Repository.ServiceTypeRepo;
import com.hymer.hymarket.dto.ServiceOfferingResponse;
import com.hymer.hymarket.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return offering.stream().map(ServiceOfferingMapper::mapDto).collect(Collectors.toList());

    }


    public List<ServiceOfferingResponse> getAllServices() {
        List<ServiceOffering> serviceOffering = serviceOfferingRepo.findAll();
        return serviceOffering.stream().map(ServiceOfferingMapper::mapDto).toList();
    }
}
