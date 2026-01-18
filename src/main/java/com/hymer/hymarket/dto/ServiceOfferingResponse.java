package com.hymer.hymarket.dto;

import lombok.Data;

@Data

public class ServiceOfferingResponse {
    private Long id;
    private double price;
    private String description;
    private String serviceTypename;
    private ProviderProfileDto provider;
}
