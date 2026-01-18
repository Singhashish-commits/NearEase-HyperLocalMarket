package com.hymer.hymarket.service;
import com.hymer.hymarket.Repository.RoleRepository;
import com.hymer.hymarket.Repository.UserRepository;
import com.hymer.hymarket.dto.JwtResponse;
import com.hymer.hymarket.dto.LoginRequest;
import com.hymer.hymarket.dto.SignUpRequest;
import com.hymer.hymarket.model.Roles;
import com.hymer.hymarket.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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

    // Autowiring the object of classes from different classes
    @Autowired
    public AuthService(UserRepository userRepo , PasswordEncoder passwordEncoder, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;

    }
// save user taking signup request
    public void saveUser(SignUpRequest signUpRequest ) {
       if(userRepo.existsByEmail((signUpRequest.getEmail()))){
           throw new RuntimeException("Email already exists");
        }
        if(userRepo.existsByEmail(signUpRequest.getUsername())){
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setUsername(signUpRequest.getUsername());
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setPhoneNumber(signUpRequest.getPhoneNUmber());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        Roles userRole = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("Role Not Found"));
        user.setRoles(Set.of(userRole));
        userRepo.save(user);

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
}
