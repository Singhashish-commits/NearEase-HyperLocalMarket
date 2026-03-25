package com.hymer.hymarket.service;

import com.hymer.hymarket.Mapper.ServiceSearchResponseDtoMapper;
import com.hymer.hymarket.Repository.ServiceSearchRepository;
import com.hymer.hymarket.Specification.ServiceSpecification;
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
    private final ServiceSearchRepository serviceSearchRepository;
    @Autowired
    public ServiceSearchService(ServiceSearchRepository serviceSearchRepository) {
        this.serviceSearchRepository = serviceSearchRepository;
    }

    public List<ServiceSearchResponseDto> searchService(ServiceSearchRequestDto serviceSearchRequestDto) {
        Specification<ServiceOffering> spec = ServiceSpecification.getSpecs(serviceSearchRequestDto);
        List<ServiceOffering> results = serviceSearchRepository.findAll(spec);
        return results.stream()
                .map(ServiceSearchResponseDtoMapper::mapDto)
                .collect(Collectors.toList());
    }



}
