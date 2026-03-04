package com.hymer.hymarket.service;

import com.hymer.hymarket.Repository.UserRepository;
import com.hymer.hymarket.dto.ApiResponse;
import com.hymer.hymarket.dto.PasswordUpdateDto;
import com.hymer.hymarket.dto.UserUpdateDto;
import com.hymer.hymarket.dto.VerifyOtpDto;
import com.hymer.hymarket.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.SecureRandom;

@Service
public class UserService {
    private  final FileUploadService fileUploadService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final MailService mailService;
    private final JwtService jwtService;

    @Autowired
    public UserService(FileUploadService fileUploadService, UserRepository userRepository, PasswordEncoder passwordEncoder, RedisService redisService, MailService mailService, JwtService jwtService) {
        this.fileUploadService = fileUploadService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisService = redisService;
        this.mailService = mailService;
        this.jwtService = jwtService;
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

    public void updateEmailRequest(VerifyOtpDto verifyOtpDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
        if(userRepository.existsByEmail(verifyOtpDto.getEmail())) {
                    throw new RuntimeException("Email already exists by other user ");
        }
        String plainOtp = String.valueOf(100000 + secureRandom.nextInt(900000));
        String hashedOtp = passwordEncoder.encode(plainOtp);
        redisService.saveValue("update_email"+verifyOtpDto.getEmail(),hashedOtp,10);
        mailService.sendMail(verifyOtpDto.getEmail(), "verify your new Email",
                "your OTP to update Your email is "+ plainOtp+ "\n valid fo r10 minutes ",
                "team HY-market" );

    }

    public ApiResponse verifyAndUpdateEmail(VerifyOtpDto verifyOtpDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
        String plainOtp = verifyOtpDto.getOtp();
        String redisKey = "update_email"+verifyOtpDto.getEmail();
        String hashedOtp = redisService.getValue(redisKey);
        if(hashedOtp==null){
            throw new RuntimeException("OTP Expired Please Try again");
        }
        if(!passwordEncoder.matches(plainOtp,hashedOtp)) {
            throw new RuntimeException("Invalid OTP");
        }
        user.setEmail(verifyOtpDto.getEmail());
        String jwtToken = jwtService.generateToken(user.getEmail());
        userRepository.save(user);

        redisService.deleteValue(redisKey);
        return new ApiResponse(true, jwtToken);

    }
}
