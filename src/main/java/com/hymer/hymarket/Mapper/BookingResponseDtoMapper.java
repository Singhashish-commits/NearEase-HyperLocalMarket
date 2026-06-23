package com.hymer.hymarket.Mapper;

import com.hymer.hymarket.dto.BookingResponseDto;
import com.hymer.hymarket.model.Booking;
import com.hymer.hymarket.model.ServiceOffering;
import com.hymer.hymarket.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BookingResponseDtoMapper {
    private static final Logger logger = LoggerFactory.getLogger(BookingResponseDtoMapper.class);
    public static BookingResponseDto mapDto(Booking booking) {
        if(booking == null) {
            return null;
        }
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        // Basics fields
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setBookingTime(booking.getBookingTime());
        bookingResponseDto.setBookingStatus(booking.getBookingStatus());
        bookingResponseDto.setBeforeImages(booking.getBeforeImages());
        bookingResponseDto.setAfterImages(booking.getAfterImages());
        bookingResponseDto.setCostumerRequest(booking.getCustomerRequest());
        bookingResponseDto.setWorkLocation(booking.getWorkLocation());
        bookingResponseDto.setScheduledTime(booking.getScheduleTime());
        bookingResponseDto.setCancellationFee(booking.getCancellationFee());
        bookingResponseDto.setPaymentStatus(booking.getPaymentStatus());


        if (booking.getServiceOffering() != null) {
            ServiceOffering offering = booking.getServiceOffering();

            if (offering.getServiceType() != null) {
                bookingResponseDto.setServiceName(offering.getServiceType().getName());
            }

            bookingResponseDto.setPrice(offering.getPrice());
        }

        bookingResponseDto.setCustomer(UserProfileDtoMapper.mapDto(booking.getCustomer()));

        bookingResponseDto.setProvider(ProviderProfileDtoMapper.mapDto(booking.getProvider()));
        return bookingResponseDto;

    }
}
