package com.hymer.hymarket.service;

import com.hymer.hymarket.model.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;


@Service
public class OtpService {
    private final SecureRandom secureRandom = new SecureRandom();
    private final RedisService redisService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public OtpService(RedisService redisService, MailService mailService, PasswordEncoder passwordEncoder) {
        this.redisService = redisService;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    public void sendSignUpOtp(String email){
                String otp = String.valueOf(100000+secureRandom.nextInt(900000));
                String hashedOtp = passwordEncoder.encode(otp);
                // will use the password encode here  to save the encoded Value
                redisService.saveValue("otp"+email, hashedOtp,10);
                mailService.sendMail(email,"NearEase – Your OTP Verification Code \n","Your Otp is: "+otp +"\n Please Don't Share with Others !"," \n Valid For 10 Minutes");


    }

    public void validateOtp( String email ,String otp){
        String redisKey = "otp"+email;
        String  storedHashedOtp = redisService.getValue(redisKey);
//        get the encoded password and match
        if(storedHashedOtp==null){
            throw new RuntimeException(" Otp Expired , Please Try Again ");
        }
        if(!passwordEncoder.matches(otp,storedHashedOtp)){
            throw new RuntimeException(" Invalid Otp! Please try again ");
        }
        redisService.deleteValue(redisKey);
        redisService.saveValue("is_verified:"+email,"true",20);
    }
    public void ResendAuthOtp(String email){
        String resendKey = "Auth_otp:"+email;
        if(redisService.getValue(resendKey) != null){
            throw new RuntimeException(" please wait 1 Minute to resend Otp ");
        }
        redisService.saveValue(resendKey,"WAIT",1);
        sendSignUpOtp(email);
    }


    public void generateBookingOtp(Booking booking){
        int optValue = 1000+secureRandom.nextInt(9000);
        String otp = String.valueOf(optValue);
        String hashedOtp= passwordEncoder.encode(otp);
        String redisKey = "booking_otp:" + booking.getId();
        redisService.saveValue(redisKey,hashedOtp,10);

        String subject = "Your Service Otp (Valid for 10 minutes)";
        String body = "Your OTP is: " + otp +
                "\n\nGive this code to the provider to mark booking confirmed." +
                "\n\nThis OTP is valid for 10 minutes.";
        mailService.sendMail(booking.getCustomer().getEmail(),subject,body,"Valid for 10 min");

    }




}
