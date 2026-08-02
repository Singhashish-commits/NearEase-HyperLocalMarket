package com.hymer.hymarket.config;

import com.hymer.hymarket.Repository.ServiceOfferingRepo;
import com.hymer.hymarket.Repository.ServiceSearchRepository;
import com.hymer.hymarket.model.ServiceOffering;
import com.hymer.hymarket.model.ServiceOfferingIndex;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class InitialSyncRunner implements CommandLineRunner {

    private final ServiceOfferingRepo serviceOfferingRepository;
    private final ServiceSearchRepository serviceSearchRepository;

    public InitialSyncRunner(ServiceOfferingRepo serviceOfferingRepository,
                             ServiceSearchRepository serviceSearchRepository) {
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.serviceSearchRepository = serviceSearchRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<ServiceOffering> all = serviceOfferingRepository.findAll();
        for (ServiceOffering offering : all) {
            ServiceOfferingIndex index = new ServiceOfferingIndex();
            index.setId(String.valueOf(offering.getId()));
            index.setPrice(offering.getPrice());
            index.setDescription(offering.getDescription());
            index.setImageUrl(offering.getImageUrl());
            index.setServiceTitle(offering.getServiceTitle());
            if (offering.getServiceType() != null) {
                index.setServiceName(offering.getServiceType().getName());
                index.setCategory(offering.getServiceType().getName());
            }
            if (offering.getProviderProfile() != null && offering.getProviderProfile().getUser() != null) {
                index.setProviderProfileName(
                        offering.getProviderProfile().getUser().getFirstName() + " " +
                                offering.getProviderProfile().getUser().getLastName()
                );
            }
            serviceSearchRepository.save(index);
        }
        System.out.println("Synced " + all.size() + " records to Elasticsearch");
    }
}
