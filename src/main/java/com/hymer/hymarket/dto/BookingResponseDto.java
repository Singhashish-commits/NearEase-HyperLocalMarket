package com.hymer.hymarket.dto;

import com.hymer.hymarket.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {

    private Long id;
    private BookingStatus bookingStatus;
    private LocalDateTime scheduledTime;
    private LocalDateTime bookingTime;
    private String workLocation;
    private String CostumerRequest;
    private String ServiceName;
    private double price;
    private UserProfileDto customer;
    private ProviderProfileDto provider;
}
