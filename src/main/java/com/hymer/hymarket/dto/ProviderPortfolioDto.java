package com.hymer.hymarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ProviderPortfolioDto {
    private Long bookingId;
    private String serviceName;
    private String category;
    private String beforeImageUrl;
    private String afterImageUrl;
}
