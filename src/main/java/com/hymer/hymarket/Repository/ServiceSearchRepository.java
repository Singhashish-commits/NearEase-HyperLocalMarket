package com.hymer.hymarket.Repository;

import com.hymer.hymarket.model.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceSearchRepository  extends JpaRepository<ServiceOffering, Long>,
        JpaSpecificationExecutor<ServiceOffering> {


}
