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
    private long completedJobs;
    private long pendingRequest;
    private double averageRating;
    //Inventory of what they provide;
    private List<ServiceOfferingResponse> activeServices;



}

