package com.hymer.hymarket.dto;

import lombok.Data;

@Data
public class PasswordUpdateDto {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
