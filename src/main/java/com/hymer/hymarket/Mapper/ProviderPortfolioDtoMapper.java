package com.hymer.hymarket.Mapper;

import com.hymer.hymarket.dto.ProviderPortfolioDto;
import com.hymer.hymarket.model.Booking;

public class ProviderPortfolioDtoMapper {

    public static ProviderPortfolioDto mapDto(Booking booking){
        if(booking == null){
            return null;
        }
        ProviderPortfolioDto dto = new ProviderPortfolioDto();
        dto.setBeforeImageUrl(booking.getBeforeImages());
        dto.setBookingId(booking.getId());
        dto.setServiceName(booking.getServiceOffering().getServiceType().getName());
        dto.setCategory(booking.getServiceOffering().getServiceType().getCategory().getName());
        dto.setAfterImageUrl(booking.getAfterImages());
        return dto;
    }
}
