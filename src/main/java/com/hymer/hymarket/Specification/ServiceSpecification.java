package com.hymer.hymarket.Specification;

import com.hymer.hymarket.dto.ServiceSearchRequestDto;
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
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    };
}
