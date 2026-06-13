package com.hymer.hymarket.service;

import com.hymer.hymarket.model.Booking;
import com.hymer.hymarket.model.ProviderProfile;
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
    private final MailTemplateService mailTemplateService;
    @Autowired
    public OtpService(RedisService redisService, MailService mailService, PasswordEncoder passwordEncoder, MailTemplateService mailTemplateService) {
        this.redisService = redisService;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.mailTemplateService = mailTemplateService;
    }

    public void sendSignUpOtp(String email){
                String otp = String.valueOf(100000+secureRandom.nextInt(900000));
                String hashedOtp = passwordEncoder.encode(otp);
                // will use the password encode here  to save the encoded Value
                redisService.saveValue("otp"+email, hashedOtp,10);
        mailService.sendMail(
                email,
                "NearEase – Your OTP Verification Code", mailTemplateService.buildOtpHtml(otp));

    }

    public void validateOtp( String email ,String otp){
        String redisKey = "otp"+email;
        String  storedHashedOtp = redisService.getValue(redisKey);
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
        String email = booking.getCustomer().getEmail();
        mailService.sendMail(
                email,
                "NearEase – Your OTP Verification Code",
                mailTemplateService.buildOtpHtml(otp)
        );
    }


    public void sendServiceDeleteOtp(Long serviceOfferingId, ProviderProfile profile) {
        String otp =  String.valueOf(100000+secureRandom.nextInt(900000));
        String hashedOtp = passwordEncoder.encode(otp);
        String redisKey= serviceOfferingId+"_delete_"+profile.getId();
        redisService.saveValue(redisKey,hashedOtp,10);
        String email = profile.getUser().getEmail();
        mailService.sendMail(email,
                "NearEase- Your Service Delete Otp", mailTemplateService.buildOtpHtml(otp));


    }

    public void sendPasswordResetOtp(String email) {
        String otp = String.valueOf(100000+secureRandom.nextInt(900000));
        String hashedOtp = passwordEncoder.encode(otp);
        redisService.saveValue("reset_otp:" + email, hashedOtp,10);
        mailService.sendMail(
                email,
                "NearEase – Account  Verification Code", mailTemplateService.buildOtpHtml(otp));


    }
}
