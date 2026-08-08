package com.hymer.hymarket;
import com.hymer.hymarket.Repository.RoleRepository;
import com.hymer.hymarket.Repository.UserRepository;
import com.hymer.hymarket.dto.ApiResponse;
import com.hymer.hymarket.dto.JwtResponse;
import com.hymer.hymarket.dto.LoginRequest;
import com.hymer.hymarket.dto.SignUpRequest;
import com.hymer.hymarket.model.Roles;
import com.hymer.hymarket.model.User;
import com.hymer.hymarket.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private OtpService otpService;
    @Mock
    private JwtService jwtService;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private AuthService authService;
    @Mock
    private UsernameBloomService usernameBloomService;
    @Mock
    private RedisService redisService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void test_SignUpOtp() { // success call the otp service
        String email = "testexample@gmail.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        authService.sendSignUpOtp(email);
        verify(otpService, times(1)).sendSignUpOtp(email);
    }

    @Test
    public void test_SignInOtp_EmailExists() {
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.sendSignUpOtp(email);
        });
        assertEquals("This Email is Already in use ,Please use another", exception.getMessage());
        verify(otpService, never()).sendSignUpOtp(anyString());
    }

    @Test
    public void test_ValidateOtp_Success() {
        String email = "test@example.com";
        String otp = "098734";
        authService.validateOtp(email,otp);
        verify(otpService, times(1)).validateOtp(email,otp);
    }

    @Test
    public void test_ValidateOtp_Failure() {
        String email= "test@example.com";
        String otp = "000000";
        doThrow(new RuntimeException("Invalid or Expired OTP"))
                .when(otpService).validateOtp(email,otp);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.validateOtp(email,otp);
        });
        assertEquals("Invalid or Expired OTP", exception.getMessage());
    }
    @Test
    public void test_SaveUser_Success_ApiResponse(){
        SignUpRequest request= new SignUpRequest();
        when(redisService.getValue("is_verified:"+"newUser@example.com")).thenReturn("true");
        when(userRepository.existsByEmail("newUser@example.com")).thenReturn(false);
        request.setEmail("newUser@example.com");
        request.setUsername("newuser123");
        request.setPassword("newPassword");
        request.setLastName("newLastName");
        request.setFirstName("newFirstName");
        request.setPhoneNumber("9876543210");
        when(userRepository.existsByUsername("newuser123".toLowerCase())).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("hashedPassword");
        Roles mockRole = new Roles();
        mockRole.setName("ROLE_USER");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(mockRole));
        ApiResponse response = authService.saveUser(request);
        assertTrue(response.isSuccess());
        assertEquals("Registration Successful!", response.getMessage());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        assertEquals("newUser@example.com", savedUser.getEmail());
        assertEquals("hashedPassword", savedUser.getPassword());
        assertTrue(savedUser.isVerified());
        verify(usernameBloomService).addUsername("newuser123");
    }
    @Test
    public void loginUser_Success_ApiResponse(){
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("hashedPassword");
        Authentication authentication = mock(Authentication.class);
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("user@example.com");
        when(authentication.getPrincipal()).thenReturn(mockUserDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        Roles mockRole = new Roles();
        mockRole.setName("ROLE_USER");
        User mockUser = new User();
        mockUser.setId(101L);
        mockUser.setEmail("user@example.com");
        mockUser.setRoles(Set.of(mockRole));

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken("user@example.com")).thenReturn("dummy.jwt.token");
        JwtResponse response = authService.loginUser(request);
        assertNotNull(response);
        assertEquals("dummy.jwt.token", response.getToken());
        assertEquals(101L, response.getId());
        assertEquals("user@example.com", response.getEmail());
        assertTrue(response.getRoles().contains("ROLE_USER"));
    }
//    @Test

}
