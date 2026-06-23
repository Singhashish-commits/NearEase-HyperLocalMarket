package com.hymer.hymarket.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProviderDashBoardDto {
    // profile Info
    private String providerName;
    private String providerEmail;
    private String ImageUrl;
    private String PhoneNo;
    // Matrices
    private Double totalEarning;
    private Long completedJobs;
    private Long pendingRequest;
    private Double averageRating;
    //Inventory of what they provide;
    private List<ServiceOfferingResponse> activeServices;



}

