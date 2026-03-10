package com.hymer.hymarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceSearchResponseDto {
    private long id;
    private String serviceName;
    private String category;
    private Double Price;
    private String Description;
    private String providerProfileName;
    private String imageUrl;

}
