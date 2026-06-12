package com.hymer.hymarket.service;

import com.hymer.hymarket.Repository.UserRepository;
import com.hymer.hymarket.dto.*;
import com.hymer.hymarket.model.Roles;
import com.hymer.hymarket.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Set;

@Service
public class UserService {
    private  final FileUploadService fileUploadService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final JwtService jwtService;
    private final OtpService otpService;

    @Autowired
    public UserService(FileUploadService fileUploadService, UserRepository userRepository, PasswordEncoder passwordEncoder, RedisService redisService,  JwtService jwtService, OtpService otpService) {
        this.fileUploadService = fileUploadService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisService = redisService;
        this.jwtService = jwtService;
        this.otpService = otpService;
    }
    private final SecureRandom secureRandom = new SecureRandom();

    public String uploadProfileImage(MultipartFile file) throws IOException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
        String userImageUrl =  fileUploadService.uploadFile(file);
        user.setProfilePictureImageUrl(userImageUrl);
        userRepository.save(user);
        return userImageUrl;

    }

    public void updatePassword(PasswordUpdateDto passwordUpdateDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
        if(!passwordEncoder.matches(passwordUpdateDto.getOldPassword(),user.getPassword())){
            throw new RuntimeException("Incorrect Old Password");
        }
        if(!passwordUpdateDto.getNewPassword().equals(passwordUpdateDto.getConfirmPassword())) {
                throw new RuntimeException("new password and confirm Password Must be same ");
        }
        if(passwordEncoder.matches(passwordUpdateDto.getNewPassword(),user.getPassword())) {
            throw new RuntimeException("old and new password must not be same ");
        }
        user.setPassword(passwordEncoder.encode(passwordUpdateDto.getNewPassword()));
        userRepository.save(user);
    }

    public void updateDetails(UserUpdateDto userUpdateDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
        if(userUpdateDto.getUsername() != null) {
            user.setUsername(userUpdateDto.getUsername());
        }
        if(userUpdateDto.getFirstName() != null) {
            user.setFirstName(userUpdateDto.getFirstName());
        }
        if(userUpdateDto.getLastName() != null) {
            user.setLastName(userUpdateDto.getLastName());
        }
        if(userUpdateDto.getPhoneNumber() != null) {
            user.setPhoneNumber(userUpdateDto.getPhoneNumber());
        }
        userRepository.save(user);
    }

    public void updateEmailRequest(OtpRequestDto otpRequestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
        if(user.getEmail().equals(otpRequestDto.getEmail())) {
                    throw new RuntimeException("Email already exists by other user ");
        }
        String plainOtp = String.valueOf(100000 + secureRandom.nextInt(900000));
        String hashedOtp = passwordEncoder.encode(plainOtp);
        redisService.saveValue("update_email"+otpRequestDto.getEmail(),hashedOtp,10);
        otpService.sendSignUpOtp(email);

    }

    public ResponseEntity<ApiResponse> verifyAndUpdateEmail(OtpRequestDto otpRequestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
        String plainOtp = otpRequestDto.getOtp();
        String redisKey = "update_email"+otpRequestDto.getEmail();
        String hashedOtp = redisService.getValue(redisKey);
        if(hashedOtp==null){
            throw new RuntimeException("OTP Expired Please Try again");
        }
        if(!passwordEncoder.matches(plainOtp,hashedOtp)) {
            throw new RuntimeException("Invalid OTP");
        }
        user.setEmail(otpRequestDto.getEmail());
        String jwtToken = jwtService.generateToken(user.getEmail());
        userRepository.save(user);
        redisService.deleteValue(redisKey);
        return new ResponseEntity<>( new ApiResponse(true, jwtToken), HttpStatus.OK);
    }


    public ResponseEntity<UserProfileDto> myDetails() {
        String email =  SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));

        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setFirstName(user.getFirstName());
        userProfileDto.setLastName(user.getLastName());
        userProfileDto.setEmail(user.getEmail());
        userProfileDto.setPhone(user.getPhoneNumber());
        userProfileDto.setImageUrl(user.getProfilePictureImageUrl());
        Set<Roles> roles = user.getRoles();
        userProfileDto.setRoles(roles);
        return new ResponseEntity<>(userProfileDto, HttpStatus.OK);
    }
}
