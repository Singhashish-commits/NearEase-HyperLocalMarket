package com.hymer.hymarket.service;

import com.hymer.hymarket.Repository.ServiceSearchRepository;
import com.hymer.hymarket.Specification.ServiceSpecification;
import com.hymer.hymarket.dto.ServiceOfferingResponse;
import com.hymer.hymarket.dto.ServiceSearchRequestDto;
import com.hymer.hymarket.dto.ServiceSearchResponseDto;
import com.hymer.hymarket.model.ServiceOffering;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceSearchService {
    private ServiceSearchRepository serviceSearchRepository;
    @Autowired
    public ServiceSearchService(ServiceSearchRepository serviceSearchRepository) {
        this.serviceSearchRepository = serviceSearchRepository;
    }

    public List<ServiceSearchResponseDto> searchService(ServiceSearchRequestDto serviceSearchRequestDto) {
        Specification<ServiceOffering> spec = ServiceSpecification.getSpecs(serviceSearchRequestDto);
        List<ServiceOffering> results = serviceSearchRepository.findAll(spec);
        return results.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ServiceSearchResponseDto mapToDto(ServiceOffering serviceOffering) {
        ServiceSearchResponseDto serviceSearchResponseDto = new ServiceSearchResponseDto();
        serviceSearchResponseDto.setId(serviceOffering.getId());
        serviceSearchResponseDto.setServiceName(serviceOffering.getServiceType().getName());
        serviceSearchResponseDto.setDescription(serviceOffering.getDescription());
        serviceSearchResponseDto.setPrice(serviceOffering.getPrice());
        serviceSearchResponseDto.setCategory(serviceOffering.getServiceType().getCategory().getName());
        serviceSearchResponseDto.setProviderProfileName(serviceOffering.getProviderProfile().getUser().getFirstName());

        return serviceSearchResponseDto;
    }


}
