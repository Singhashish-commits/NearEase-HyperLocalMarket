package com.hymer.hymarket.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Email Can't be blank")
    @Email(message = "Please Provide a valid Email")
    private String email;

    @NotBlank(message = "password can't be blank")
    private String password;
}
