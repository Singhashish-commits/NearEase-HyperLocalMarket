package com.hymer.hymarket.controller;

import com.hymer.hymarket.dto.*;
import com.hymer.hymarket.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/user-update")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/profile-image")
    public ResponseEntity<?> updateProfilePicture(@RequestParam("file") MultipartFile file) throws IOException {
            String imageUrl = userService.uploadProfileImage(file);
        return ResponseEntity.ok(Map.of(
                "message", "Profile picture updated successfully!",
                "imageUrl", imageUrl
        ));

    }

    @PostMapping("/changePassword")
    public ResponseEntity<ApiResponse> updatePassword(@RequestBody PasswordUpdateDto passwordUpdateDto) {
        userService.updatePassword(passwordUpdateDto);
        return ResponseEntity.ok(new ApiResponse(true, "Password updated successfully !! login Again "));
    }

    @PostMapping("/update-details")
    public ResponseEntity<ApiResponse> updateDetails(@RequestBody UserUpdateDto userUpdateDto) {
        userService.updateDetails(userUpdateDto);
        return ResponseEntity.ok(
                new ApiResponse(true,"Details updated successfully !!")
        );
    }

    @PostMapping("request-email-update")
    public ResponseEntity<ApiResponse> requestEmailUpdate(@RequestBody OtpRequestDto otpRequestDto) {
        userService.updateEmailRequest(otpRequestDto);
        return ResponseEntity.ok(new ApiResponse(true, "Otp Sent to your new Email"));
    }

    @PostMapping("/verify-email-update")
    public ResponseEntity<ApiResponse> verifyEmailUpdate(@RequestBody OtpRequestDto otpRequestDto) {
     return  userService.verifyAndUpdateEmail(otpRequestDto);

    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> myDetails(){
        return userService.myDetails();
    }
}
