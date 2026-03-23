package com.hymer.hymarket.Specification;

import com.hymer.hymarket.dto.ServiceSearchRequestDto;
import com.hymer.hymarket.model.ProviderProfile;
import com.hymer.hymarket.model.ServiceCategory;
import com.hymer.hymarket.model.ServiceOffering;
import com.hymer.hymarket.model.ServiceType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
@Component
public class ServiceSpecification {
    public static Specification<ServiceOffering> getSpecs(ServiceSearchRequestDto serviceSearchRequestDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            //Filter By Category Name
            //Service Offering->Service Type-> Service Category
            if(serviceSearchRequestDto.getCategory()!=null && !serviceSearchRequestDto.getCategory().isEmpty()){
                Join<ServiceOffering, ServiceType> typeJoin = root.join("serviceType");
                Join<ServiceType, ServiceCategory> categoryJoin = typeJoin.join("category");

                predicates.add(criteriaBuilder.equal(categoryJoin.get("name"), serviceSearchRequestDto.getCategory()));
            }

            // Filter By price Range
            if(serviceSearchRequestDto.getMinPrice()!=null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), serviceSearchRequestDto.getMinPrice()));
            }
            if(serviceSearchRequestDto.getMaxPrice()!=null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), serviceSearchRequestDto.getMaxPrice()));
            }
            //search using the Keyword , SmartSearch
            if(serviceSearchRequestDto.getMinPrice()!=null&& ! serviceSearchRequestDto.getSearchKeyword().isEmpty()){
                String keyword = "%"+ serviceSearchRequestDto.getSearchKeyword().toLowerCase()+"%";
                Join<ServiceOffering , ServiceType> typeJoin = root.join("serviceType");
                Predicate descMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("Description")), keyword);
                Predicate typeNameMatch = criteriaBuilder.like(criteriaBuilder.lower(typeJoin.get("name")), keyword);

                predicates.add(criteriaBuilder.or(descMatch, typeNameMatch));
            }
            //Geo-Spatial Search
            if(serviceSearchRequestDto.getUserLat()!=null && serviceSearchRequestDto.getUserLng()!=null && serviceSearchRequestDto.getRadiusKm()!=null){
                final double earthRadiusKm = 6371.01;
                final double degree_Distance = 111.0;
                // calculate the min and the max Latitude
                double latDelta = serviceSearchRequestDto.getRadiusKm()/degree_Distance;
                double minLat = serviceSearchRequestDto.getUserLat()-latDelta;
                double maxLat = serviceSearchRequestDto.getUserLat()+latDelta;

                // Calculating the user max and min Longitude
                double lanDelta = serviceSearchRequestDto
                        .getRadiusKm()/(degree_Distance * Math.cos(Math.toRadians(serviceSearchRequestDto. getUserLat())));
                double minLng = serviceSearchRequestDto.getUserLng()-lanDelta;
                double maxLng = serviceSearchRequestDto.getUserLng()+lanDelta;

                // Join Service Offering -> providerProfile to access the data
                Join<ServiceOffering, ProviderProfile> providerJoin = root.join("providerProfile");
                predicates.add(criteriaBuilder.between(providerJoin.get("latitude"), minLat, maxLat));
                predicates.add(criteriaBuilder.between(providerJoin.get("longitude"), minLng, maxLng));
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
