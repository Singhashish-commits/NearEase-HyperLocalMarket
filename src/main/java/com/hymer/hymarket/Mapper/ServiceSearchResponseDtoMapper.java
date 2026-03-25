package com.hymer.hymarket.Mapper;

import com.hymer.hymarket.dto.ServiceSearchResponseDto;
import com.hymer.hymarket.model.ServiceOffering;

public class ServiceSearchResponseDtoMapper {
    public static ServiceSearchResponseDto mapDto(ServiceOffering serviceOffering) {
        if(serviceOffering==null){
            return null;
        }

        ServiceSearchResponseDto serviceSearchResponseDto = new ServiceSearchResponseDto();
        serviceSearchResponseDto.setId(serviceOffering.getId());
        serviceSearchResponseDto.setServiceName(serviceOffering.getServiceType().getName());
        serviceSearchResponseDto.setDescription(serviceOffering.getDescription());
        serviceSearchResponseDto.setPrice(serviceOffering.getPrice());
        serviceSearchResponseDto.setCategory(serviceOffering.getServiceType().getCategory().getName());
        serviceSearchResponseDto.setProviderProfileName(serviceOffering.getProviderProfile().getUser().getFirstName());
        serviceSearchResponseDto.setImageUrl(serviceOffering.getImageUrl());

        return serviceSearchResponseDto;
    }
}
