package com.hymer.hymarket.Repository;

import com.hymer.hymarket.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceTypeRepo extends JpaRepository<ServiceType,Long> {
    Optional<ServiceType> findById(Long id);


    List<ServiceType> findByCategoryId(long id);

//    List<ServiceType> findAllService(long id);

}
