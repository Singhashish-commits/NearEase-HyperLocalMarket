package com.hymer.hymarket.dto;


import lombok.Data;

@Data
public class VerifyOtpDto {
    private String email;
    private String otp;
}
