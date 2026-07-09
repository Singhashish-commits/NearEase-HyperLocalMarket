package com.hymer.hymarket.Mapper;

import com.hymer.hymarket.dto.ServiceSearchResponseDto;
import com.hymer.hymarket.model.ServiceOffering;
import com.hymer.hymarket.model.ServiceOfferingIndex;

public class ServiceSearchResponseDtoMapper {
    public static ServiceSearchResponseDto mapDto(ServiceOfferingIndex index) {
        ServiceSearchResponseDto dto = new ServiceSearchResponseDto();

        // Convert the Elastic String ID back to a Long for your frontend
        if (index.getId() != null) {
            dto.setId(Long.valueOf(index.getId()));
        }

        dto.setServiceName(index.getServiceName());
        dto.setServiceTitle(index.getServiceTitle());
        dto.setCategory(index.getCategory());
        dto.setPrice(index.getPrice());
        dto.setDescription(index.getDescription());
        dto.setProviderProfileName(index.getProviderProfileName());
        dto.setImageUrl(index.getImageUrl());

        return dto;
    }
}
