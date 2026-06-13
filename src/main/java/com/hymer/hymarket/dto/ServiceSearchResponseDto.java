package com.hymer.hymarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceSearchResponseDto {
    private Long id;
    private String serviceName;
    private String category;
    private Double price;
    private String description;
    private String providerProfileName;
    private String imageUrl;

}
