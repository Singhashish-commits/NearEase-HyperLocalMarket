package com.hymer.hymarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDto {
    String email;
    String otp;
    String newPassword;
    String confirmNewPassword;
}
