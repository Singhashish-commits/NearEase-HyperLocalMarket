package com.hymer.hymarket.dto;

import lombok.Data;

@Data
public class ProviderProfileDto {
    private Long id;
    private String bio;
    private String skill;
    private String experience;
    private String address;
    private Boolean verified;
    private UserProfileDto user;



}
