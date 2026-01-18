package com.hymer.hymarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {
    // Customer need not to, send everything what they need to send is that, I want this 101 order-on my home at ths time
    private long serviceOfferingId;
    private LocalDateTime scheduleTime;
    private String workLocation;
    private String customerRequest;
}
