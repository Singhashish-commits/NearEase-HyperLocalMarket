package com.hymer.hymarket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceOfferingRequest {
    private long serviceTypeId;
    private double price;
    private String description;

}

