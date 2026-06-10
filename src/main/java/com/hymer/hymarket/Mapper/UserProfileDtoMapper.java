package com.hymer.hymarket.Mapper;

import com.hymer.hymarket.dto.UserProfileDto;
import com.hymer.hymarket.model.User;

public class UserProfileDtoMapper {
    public static UserProfileDto mapDto(User user){
        if(user == null) return null;
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setId(user.getId());

            userProfileDto.setPhone(user.getPhoneNumber());
            userProfileDto.setEmail(user.getEmail());

        userProfileDto.setFirstName(user.getFirstName());
        userProfileDto.setLastName(user.getLastName());
        userProfileDto.setImageUrl(user.getProfilePictureImageUrl());
        userProfileDto.setRoles(user.getRoles());
        return userProfileDto;

    }



}
