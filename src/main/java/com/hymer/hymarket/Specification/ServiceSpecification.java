package com.hymer.hymarket.Specification;

import com.hymer.hymarket.dto.ServiceSearchRequestDto;
import com.hymer.hymarket.model.*;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
@Component
public class ServiceSpecification {
    public static Specification<ServiceOffering> getSpecs(ServiceSearchRequestDto dto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<ServiceOffering, ServiceType>     typeJoin     = root.join("serviceType",     JoinType.LEFT);
            Join<ServiceType,     ServiceCategory> categoryJoin = typeJoin.join("category",    JoinType.LEFT);
            Join<ServiceOffering, ProviderProfile> providerJoin = root.join("providerProfile", JoinType.LEFT);

            if (dto.getCategory() != null && !dto.getCategory().isEmpty()) {
                predicates.add(cb.equal(categoryJoin.get("name"), dto.getCategory()));
            }

            if (dto.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), dto.getMinPrice()));
            }
            if (dto.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), dto.getMaxPrice()));
            }

            if (dto.getMinRating() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), dto.getMinRating()));
            }

            if (dto.getSearchKeyword() != null && !dto.getSearchKeyword().isEmpty()) {
                String keyword = "%" + dto.getSearchKeyword().toLowerCase() + "%";

                Predicate descMatch         = cb.like(cb.lower(root.get("serviceTitle")),              keyword);
                Predicate typeNameMatch     = cb.like(cb.lower(typeJoin.get("name")),                 keyword);
                Predicate categoryNameMatch = cb.like(cb.lower(categoryJoin.get("name")),             keyword);
                Predicate providerNameMatch = cb.like(cb.lower(providerJoin.get("city")),     keyword);

                predicates.add(cb.or(descMatch, typeNameMatch, categoryNameMatch, providerNameMatch));

            }

            if (dto.getUserLat() != null && dto.getUserLng() != null && dto.getRadiusKm() != null) {

                // Step A: fast bounding box filter (hits DB index)
                double latDelta = dto.getRadiusKm() / 111.0;
                double lngDelta = dto.getRadiusKm() / (111.0 * Math.cos(Math.toRadians(dto.getUserLat())));

                double minLat = dto.getUserLat() - latDelta;
                double maxLat = dto.getUserLat() + latDelta;
                double minLng = dto.getUserLng() - lngDelta;
                double maxLng = dto.getUserLng() + lngDelta;


                predicates.add(cb.between(
                        providerJoin.<String>get("latitude"),
                        String.valueOf(minLat),
                        String.valueOf(maxLat)
                ));
                predicates.add(cb.between(
                        providerJoin.<String>get("longitude"),
                        String.valueOf(minLng),
                        String.valueOf(maxLng)
                ));

            }
            if (query != null) {
                if (dto.getSortBy() != null) {
                    boolean asc = "asc".equalsIgnoreCase(dto.getSortDirn());
                    switch (dto.getSortBy()) {
                        case "price"  -> query.orderBy(asc ? cb.asc(root.get("price"))  : cb.desc(root.get("price")));
                        case "rating" -> query.orderBy(asc ? cb.asc(root.get("rating")) : cb.desc(root.get("rating")));
                    }
                }
                query.distinct(true);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
