package com.hymer.hymarket.controller;
import com.hymer.hymarket.dto.*;
import jakarta.validation.Valid;
import com.hymer.hymarket.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin()
@RequestMapping("/api/auth")
public class AuthController {
    private  final AuthService authService;
    @Autowired
    public AuthController(AuthService authService) {
            this.authService = authService;
    }



    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody OtpRequestDto otpRequestDto){
        authService.sendSignUpOtp(otpRequestDto.getEmail());
        return ResponseEntity.ok("Otp Sent to the Email Successfully");

    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestBody OtpRequestDto otpRequestDto){
        authService.resendOtp(otpRequestDto.getEmail());
        return ResponseEntity.ok("Otp Sent to the Email Successfully");
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<String> validateOtp(@RequestBody OtpRequestDto otpRequestDto){
        authService.validateOtp(otpRequestDto.getEmail(),otpRequestDto.getOtp());
        return ResponseEntity.ok("Email Verified Successfully");
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
            authService.saveUser(signUpRequest);
            ApiResponse response = new ApiResponse(true, "Sign up successful");
            return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
            // The service now returns the fully built JwtResponse
            JwtResponse jwtResponse = authService.loginUser(loginRequest);
            return ResponseEntity.ok(jwtResponse);
    }
}
