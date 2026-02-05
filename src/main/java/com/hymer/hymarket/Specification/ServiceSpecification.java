package com.hymer.hymarket.Specification;

import com.hymer.hymarket.dto.ServiceSearchDto;
import com.hymer.hymarket.model.ServiceCategory;
import com.hymer.hymarket.model.ServiceOffering;
import com.hymer.hymarket.model.ServiceType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Predicates;
import org.springframework.security.core.parameters.P;

import java.util.ArrayList;
import java.util.List;

public class ServiceSpecification {
    public static Specification<ServiceOffering> getSpecs(ServiceSearchDto serviceSearchDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            //Filter By Category Name
            //Service Offering->Service Type-> Service Category
            if(serviceSearchDto.getCategory()!=null && !serviceSearchDto.getCategory().isEmpty()){
                Join<ServiceOffering, ServiceType> typeJoin = root.join("serviceType");
                Join<ServiceType, ServiceCategory> categoryJoin = typeJoin.join("category");

                predicates.add(criteriaBuilder.equal(categoryJoin.get("name"),serviceSearchDto.getCategory()));
            }

            // Filter By price Range
            if(serviceSearchDto.getMinPrice()!=null){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"),serviceSearchDto.getMinPrice()));
            }
            if(serviceSearchDto.getMaxPrice()!=null){
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"),serviceSearchDto.getMaxPrice()));

            }
            //search using the Keyword , SmartSearch
            if(serviceSearchDto.getMinPrice()!=null&& ! serviceSearchDto.getSearchKeyword().isEmpty()){
                String keyword = "%"+ serviceSearchDto.getSearchKeyword().toLowerCase()+"%";
                Join<ServiceOffering , ServiceType> typeJoin = root.join("serviceType");
                Predicate descMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("Description")), keyword);
                Predicate typeNameMatch = criteriaBuilder.like(criteriaBuilder.lower(typeJoin.get("name")), keyword);

                predicates.add(criteriaBuilder.or(descMatch, typeNameMatch));
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    };
}
