package com.hymer.hymarket.service;
import com.hymer.hymarket.Repository.RoleRepository;
import com.hymer.hymarket.Repository.UserRepository;
import com.hymer.hymarket.dto.*;
import com.hymer.hymarket.model.Roles;
import com.hymer.hymarket.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepo;
    private final RoleRepository roleRepository;
    private final  AuthenticationManager authenticationManager;
    private  final JwtService jwtService;
    private final RedisService redisService;
    private final MailService mailService;
    private final UsernameBloomService usernameBloomService;
    private final OtpService otpService;

    // Autowiring the object of classes from different classes
    @Autowired
    public AuthService(UserRepository userRepo , PasswordEncoder passwordEncoder, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtService jwtService, RedisService redisService, MailService mailService, UsernameBloomService usernameBloomService, OtpService otpService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.redisService = redisService;
        this.mailService = mailService;
        this.usernameBloomService = usernameBloomService;
        this.otpService = otpService;
    }

// Otp for SignUp
    public void sendSignUpOtp(String email){
        if(userRepo.existsByEmail(email)){
            throw new RuntimeException("This Email is Already in use ,Please use another");
        }
            otpService.sendSignUpOtp(email);
    }
    // Validation of Otp
    public void validateOtp(String email, String otp){

        otpService.validateOtp(email,otp);


    }
    public void resendOtp(String email){
        otpService.ResendAuthOtp(email);
    }

    // save user taking signup request and after verifying Otp;
    public ApiResponse saveUser(SignUpRequest signUpRequest ) {

           String varificationStatus = redisService.getValue("is_verified:"+signUpRequest.getEmail());
           if(varificationStatus==null || !varificationStatus.equals("true")){
               throw new RuntimeException("Email not Verified! Please Verify Your Email");
           }

        if(userRepo.existsByEmail((signUpRequest.getEmail()))){
           throw new RuntimeException("Email already exists");
        }
        if(userRepo.existsByUsername(signUpRequest.getUsername())){
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setUsername(signUpRequest.getUsername());
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        user.setVerified(true);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        Roles userRole = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("Role Not Found"));
        user.setRoles(Set.of(userRole));
        userRepo.save(user);
        usernameBloomService.addUsername(user.getUsername().toLowerCase());
        return new ApiResponse(true,"Registration Successful!");

    }
// Login for the already exist user
    public  JwtResponse loginUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // 2. Get user details from the authentication
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();

        // 3. Find the User (using the safe orElseThrow)
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 4. Generate BOTH tokens
        String token = jwtService.generateToken(userEmail);
//        String refreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();

        // 5. Get roles (Fixing the typo: Role::getName)
        List<String> roles = user.getRoles().stream()
                .map(Roles::getName) // <-- Use singular "Role"
                .collect(Collectors.toList());

        // 6. Return the pure JwtResponse DTO (using the 5-argument constructor)
        return new JwtResponse(token,  user.getId(), user.getEmail(), roles);
    }


    public boolean isUserNameAvailable(String username) {
        String normalizedUsername = username.toLowerCase();
       if(!usernameBloomService.mightExist(normalizedUsername)){
           return true;
       }
       return !userRepo.existsByUsername(username.toLowerCase());
    }

    public ApiResponse AccountRecoveryMail(OtpRequestDto otpRequestDto) {
        String email = otpRequestDto.getEmail();
        if(userRepo.existsByEmail(email)){
            otpService.sendPasswordResetOtp(email);
        }

        return new ApiResponse(true,
                "If an account with that email exists, a recovery OTP has been sent.");
    }

    public ApiResponse changePassword(ChangePasswordDto dto) {
        String email = dto.getEmail();
        String key = "reset_otp:" + email;
         String hashedOtp = redisService.getValue(key);
         if(hashedOtp==null){
             throw new RuntimeException("OTP Expired! Please try requesting a new one.");
         }
        if (!passwordEncoder.matches(dto.getOtp(), hashedOtp)) {
            throw new IllegalArgumentException("Invalid OTP.");
        }
         if(!dto.getNewPassword().equals(dto.getConfirmNewPassword())){
             throw new IllegalArgumentException("Password must math with  ConfirmPassword");
         }
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User Not Found"));
         user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
         userRepo.save(user);
         redisService.deleteValue(key);
         return new ApiResponse(true,"Password Changed Successfully please Login ");

    }
}
