package com.hymer.hymarket.Mapper;

import com.hymer.hymarket.dto.ServiceOfferingResponse;
import com.hymer.hymarket.model.ServiceOffering;

public class ServiceOfferingMapper {
    public static ServiceOfferingResponse mapDto(ServiceOffering serviceOffering) {
        if(serviceOffering == null) {
            return null;
        }
        ServiceOfferingResponse serviceOfferingResponse = new ServiceOfferingResponse();
        serviceOfferingResponse.setId(serviceOffering.getId());
        serviceOfferingResponse.setProvider(ProviderProfileDtoMapper.mapDto(serviceOffering.getProviderProfile()));
        serviceOfferingResponse.setServiceTypename(serviceOffering.getServiceType().getName());
        serviceOfferingResponse.setPrice(serviceOffering.getPrice());
        serviceOfferingResponse.setDescription(serviceOffering.getDescription());
        serviceOfferingResponse.setImageUrl(serviceOffering.getImageUrl());
        serviceOfferingResponse.setServiceTypename(serviceOffering.getServiceType().getName());
        serviceOfferingResponse.setServiceTitle(serviceOffering.getServiceTitle());
        return serviceOfferingResponse;
    }

}
