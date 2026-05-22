package com.hymer.hymarket.dto;

import com.hymer.hymarket.model.Roles;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UserProfileDto {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String imageUrl;
    Set<Roles> roles;


}
