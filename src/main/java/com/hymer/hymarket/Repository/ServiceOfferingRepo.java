package com.hymer.hymarket.Repository;

import com.hymer.hymarket.model.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceOfferingRepo  extends JpaRepository<ServiceOffering,Integer> {
    List<ServiceOffering> findByServiceTypeId(Long serviceType);

//    List<ServiceOfferingResponse> findByServiceTypeId(Long typeId);
    Optional<ServiceOffering> findById(Integer id);
}
