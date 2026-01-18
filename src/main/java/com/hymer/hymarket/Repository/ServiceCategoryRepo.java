package com.hymer.hymarket.Repository;

import com.hymer.hymarket.model.ServiceCategory;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ServiceCategoryRepo  extends JpaRepository<ServiceCategory,Integer> {
    Optional<ServiceCategory> findById(Integer id);

    Optional<ServiceCategory> findByName(String categoryName);
}
