package com.hymer.hymarket.service;

import com.hymer.hymarket.Repository.ServiceSearchRepository;
import com.hymer.hymarket.model.ServiceOffering;
import com.hymer.hymarket.model.ServiceOfferingIndex;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchSyncListener {
    private static ServiceSearchRepository serviceSearchRepository;

    @Autowired
    public void setServiceSearchRepository(ServiceSearchRepository repo) {
        ElasticsearchSyncListener.serviceSearchRepository = repo;
    }


    @PostPersist
    @PostUpdate
    public void syncDataToElastic(ServiceOffering offering) {
        ServiceOfferingIndex index = new ServiceOfferingIndex();
        index.setId(String.valueOf(offering.getId()));
        index.setPrice(offering.getPrice());
        index.setDescription(offering.getDescription());
        index.setImageUrl(offering.getImageUrl());

        // Assuming your Postgres entity has a getTitle() or similar method
        index.setServiceTitle(offering.getServiceTitle());
        if(offering.getServiceTitle()!=null && !offering.getServiceTitle().trim().isEmpty()){
            index.setServiceTitle(offering.getServiceTitle());
        }

        // Flattening the Category
        if (offering.getServiceType() != null) {
            index.setCategory(offering.getServiceType().getName());
        }

        // Flattening the Provider Name (Adjust this chain to match your exact entity structure)
        if (offering.getProviderProfile() != null && offering.getProviderProfile().getUser() != null) {
            index.setProviderProfileName(offering.getProviderProfile().getUser().getFirstName()+" "+offering.getProviderProfile().getUser().getLastName());
        }

        serviceSearchRepository.save(index);
    }
}
