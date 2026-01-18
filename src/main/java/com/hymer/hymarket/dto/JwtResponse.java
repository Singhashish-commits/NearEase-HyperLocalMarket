package com.hymer.hymarket.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Data
@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type ="Bearer";
    private long id;
    private String email;
    private List<String> roles;

    public JwtResponse(String AccessToken,  Long id, String email, List<String> roles) {
        this.token = AccessToken;
        this.id = id;
        this.email = email;
        this.roles = roles;

    }


}
