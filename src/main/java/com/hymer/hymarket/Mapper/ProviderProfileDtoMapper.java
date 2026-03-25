package com.hymer.hymarket.Mapper;

import com.hymer.hymarket.dto.ProviderProfileDto;
import com.hymer.hymarket.model.ProviderProfile;

public class ProviderProfileDtoMapper {
    public static ProviderProfileDto mapDto(ProviderProfile providerProfile) {
        if(providerProfile == null) return null;
        ProviderProfileDto providerProfileDto = new ProviderProfileDto();
        providerProfileDto.setId(providerProfile.getId());
        providerProfileDto.setSkill(providerProfile.getSkills());
        providerProfileDto.setBio(providerProfile.getBio());
        providerProfileDto.setAddress(providerProfile.getAddress());
        providerProfileDto.setExperience(providerProfile.getExperience());
        providerProfileDto.setUser(UserProfileDtoMapper.matDto(providerProfile.getUser()));
        providerProfileDto.setVerified(providerProfile.isVerified());
        return providerProfileDto;

    }
}
