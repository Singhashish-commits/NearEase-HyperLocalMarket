package com.hymer.hymarket.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
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
    @Size(max = 1000, message = "Reply cannot exceed 1000 characters")
    private String providerReply;
    private LocalDateTime repliedAt;

}
