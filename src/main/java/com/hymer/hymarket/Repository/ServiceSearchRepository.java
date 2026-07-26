package com.hymer.hymarket.Repository;

import com.hymer.hymarket.model.ServiceOffering;
import com.hymer.hymarket.model.ServiceOfferingIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

//@Repository
//public interface ServiceSearchRepository  extends JpaRepository<ServiceOffering, Long>,
//        JpaSpecificationExecutor<ServiceOffering> {
//
//
//}
@Repository
public interface ServiceSearchRepository extends ElasticsearchRepository<ServiceOfferingIndex,String> {
    List<ServiceOfferingIndex> findByDescriptionAndProviderProfileName(
            String description,
            String providerProfileName
    );
    List<ServiceOfferingIndex> findByProviderProfileName(String providerProfileName);
    List<ServiceOfferingIndex> findByServiceTitleContaining(String keyword);}
