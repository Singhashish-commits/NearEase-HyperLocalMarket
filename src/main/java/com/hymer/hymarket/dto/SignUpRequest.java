package com.hymer.hymarket.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @Email(message = "Email can't be Blank")
    @NotBlank(message = "Email can't Be Blank")
    private String email;
    @NotBlank(message = "username Can't Be Blank ")
    @Size(min = 5, max = 20 ,message = "username must be between 5 to 20 character")
    private String username;
    @NotBlank(message = "Password can't be Blank")
    private String password;
    @NotBlank(message =" first name can't be blank ")
    private String firstName;
    private String lastName;
    @NotBlank(message = "phone no cant be blank")
    private String PhoneNUmber;

}
