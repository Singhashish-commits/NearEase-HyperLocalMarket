package com.hymer.hymarket.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProviderResponseDto {
    private Long id;
    private int rating;
    private String comment;
    private Long BookingId;
    private LocalDateTime bookingDate;
    private String serviceName;
    private String customerName;

}
