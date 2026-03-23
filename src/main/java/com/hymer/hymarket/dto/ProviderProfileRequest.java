package com.hymer.hymarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProviderProfileRequest {

    private String bio;
    private String skills;
    private String experience;
    private String address;
    private String city;
    private String state;
    private Long pinCode;
    private  String latitude;
    private String  longitude;
}
