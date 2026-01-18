package com.hymer.hymarket.controller;
import jakarta.validation.Valid;
import com.hymer.hymarket.dto.ApiResponse;
import com.hymer.hymarket.dto.JwtResponse;
import com.hymer.hymarket.dto.LoginRequest;
import com.hymer.hymarket.dto.SignUpRequest;
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

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            authService.saveUser(signUpRequest);
            ApiResponse response = new ApiResponse(true, "Sign up successful");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        try {
            // The service now returns the fully built JwtResponse
            JwtResponse jwtResponse = authService.loginUser(loginRequest);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new ApiResponse(false, "Login Failed"));
        }
    }
}
