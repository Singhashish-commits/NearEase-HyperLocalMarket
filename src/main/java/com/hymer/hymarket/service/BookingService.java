package com.hymer.hymarket.service;

import com.hymer.hymarket.Mapper.BookingResponseDtoMapper;
import com.hymer.hymarket.Repository.BookingRepo;
import com.hymer.hymarket.Repository.ServiceOfferingRepo;
import com.hymer.hymarket.Repository.UserRepository;
import com.hymer.hymarket.dto.ApiResponse;
import com.hymer.hymarket.dto.BookingRequestDto;
import com.hymer.hymarket.dto.BookingResponseDto;
import com.hymer.hymarket.dto.ServiceOfferingResponse;
import com.hymer.hymarket.model.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookingService{
    private static final double PLATFORM_COMMISSION_RATE = 0.10;
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final  BookingRepo bookingRepo;
    private final  ServiceOfferingRepo serviceOfferingRepo;
    private  final UserRepository userRepository;
    private final MailService mailService;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadService fileUploadService;
    private final OtpService otpService;
    private final BookingNotificationManager notificationManager;

    @Autowired
    public BookingService(BookingRepo bookingRepo, ServiceOfferingRepo serviceOfferingRepo, UserRepository userRepository, MailService mailService, RedisService redisService, PasswordEncoder passwordEncoder, FileUploadService fileUploadService, OtpService otpService, BookingNotificationManager notificationManager) {
        this.bookingRepo = bookingRepo;
        this.serviceOfferingRepo = serviceOfferingRepo;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.redisService = redisService;
        this.passwordEncoder = passwordEncoder;
        this.fileUploadService = fileUploadService;
        this.otpService = otpService;
        this.notificationManager = notificationManager;
    }

    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User customer = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
        ServiceOffering offering  = serviceOfferingRepo.findById((int)bookingRequestDto.getServiceOfferingId())
                .orElseThrow(()-> new RuntimeException("Service not Find"));
        Long providerUserId = offering.getProviderProfile().getUser().getId();
        if(Objects.equals(customer.getId(), providerUserId)){
            throw new RuntimeException("you can't book your own services");
        }

        Booking booking = new Booking();
        booking.setCustomer( customer );
        booking.setServiceOffering(offering);
        booking.setProvider(offering.getProviderProfile());
        booking.setBookingTime(LocalDateTime.now());
        booking.setCustomerRequest(bookingRequestDto.getCustomerRequest());
        booking.setScheduleTime(bookingRequestDto.getScheduleTime());
        booking.setWorkLocation(bookingRequestDto.getWorkLocation());
        booking.setPrice(offering.getPrice());
        Booking savedBooking = bookingRepo.save(booking);
        if (savedBooking.getCustomer() == null ||
                savedBooking.getServiceOffering() == null ||
                savedBooking.getProvider() == null) {

            logger.warn("Incomplete booking detected. Id: {}, customer: {}, serviceOffering: {}, provider: {}",
                    savedBooking.getId(),
                    savedBooking.getCustomer() != null,
                    savedBooking.getServiceOffering() != null,
                    savedBooking.getProvider() != null);
        }
        return BookingResponseDtoMapper.mapDto(savedBooking);
    }

    public List<BookingResponseDto> customerBookings(){
        // the list of booking that the customer is booking
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow( ()->new RuntimeException("User not Found"));
        List<Booking>bookings = bookingRepo.findByCustomerId(user.getId());

            return bookings.stream()
                    .map(BookingResponseDtoMapper::mapDto)
                    .collect(Collectors.toList());
    }

    public List<BookingResponseDto> providerBookings(){
        // booking that the provider got from the customer
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException(" User Not Found"));
        if(user.getProviderProfile()==null) throw new RuntimeException("Provider Profile Not Found");
        List<Booking> bookings = bookingRepo.findByProviderId(user.getProviderProfile().getId());
        return bookings.stream()
                    .map(BookingResponseDtoMapper::mapDto)
                    .collect(Collectors.toList());

    }
//    THe method for the Provider so that he can mark the booking to make it confirm-accept
    @Transactional
    public BookingResponseDto updateBookingStatus(Long bookingId, BookingStatus newStatus){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(()-> new RuntimeException("Booking not found "));
       if(!booking.getProvider().getUser().getId().equals(currentUser.getId())){
           throw new RuntimeException("Not Authorized to manage this Booking");
       }
        BookingStatus current = booking.getBookingStatus();
        if (current == BookingStatus.COMPLETED
                || current == BookingStatus.REJECTED
                || current == BookingStatus.CANCELLED_BY_PROVIDER
                || current == BookingStatus.CANCELLED) {

            throw new RuntimeException(
                    "Booking can no longer be modified");
        }
        if (newStatus == BookingStatus.COMPLETED) {
            throw new RuntimeException(
                    "Booking completion requires OTP verification");
        }
        if (newStatus == BookingStatus.PENDING) {
            throw new RuntimeException(
                    "Cannot manually set booking to PENDING");
        }
        if (current == BookingStatus.PENDING) {

            if (newStatus != BookingStatus.CONFIRMED
                    && newStatus != BookingStatus.REJECTED) {

                throw new RuntimeException(
                        "Pending booking can only be confirmed or rejected");
            }
        }
        if (current == BookingStatus.CONFIRMED) {
            if (newStatus != BookingStatus.CANCELLED_BY_PROVIDER) {
                throw new RuntimeException(
                        "Confirmed booking can only be cancelled by provider");
            }
        }

        booking.setBookingStatus(newStatus);
        if(newStatus == BookingStatus.CONFIRMED){
            booking.setPlatformCommission(booking.getPrice()*PLATFORM_COMMISSION_RATE);
        }
        Booking updatedBooking = bookingRepo.save(booking);
        notificationManager.triggerNotification(booking,newStatus);
        return BookingResponseDtoMapper.mapDto(updatedBooking);
    }

@Transactional
 public BookingResponseDto completeBookingWithOtp(Long bookingId,
                                                  String otp,
                                                  MultipartFile beforeImage,
                                                  MultipartFile afterImage) throws IOException {
        Booking booking=bookingRepo.findById(bookingId)
                .orElseThrow(()-> new RuntimeException("Booking Not Found"));
     String LoggedInProvider =SecurityContextHolder.getContext().getAuthentication().getName();
     String currProvider = booking.getProvider().getUser().getEmail();
     if(!currProvider.equals(LoggedInProvider)){
         throw new RuntimeException("Unauthorized: Only the assigned provider can complete this booking.\"");
     }
        if(!booking.getBookingStatus().equals(BookingStatus.CONFIRMED)){
            throw new RuntimeException("Booking is not Ready for Completion");
        }


        String redisKey = "booking_otp:"+booking.getId();
        String hashedOtp = redisService.getValue(redisKey);
        if(hashedOtp==null){
            throw new RuntimeException("Otp Expired ! please ask customer to resend  the Otp");
        }
        if(!passwordEncoder.matches(otp,hashedOtp)){
            throw new RuntimeException("Invalid Otp, Please try Again Later");
        }

        if(beforeImage!=null && !beforeImage.isEmpty()){
            String beforeImageUrl =  fileUploadService.uploadFile(beforeImage);
            booking.setBeforeImages(beforeImageUrl);

        }
        if(afterImage!=null && !afterImage.isEmpty()){
            String afterImageUrl =  fileUploadService.uploadFile(afterImage);
            booking.setAfterImages(afterImageUrl);
        }
     booking.setBookingStatus(BookingStatus.COMPLETED);
        redisService.deleteValue(redisKey);
        notificationManager.triggerNotification(booking,BookingStatus.COMPLETED);
     return BookingResponseDtoMapper.mapDto(booking);
 }

 // this is for the customer
    @Transactional
 public ApiResponse requestCancellation(Long bookingId){
        String email =  SecurityContextHolder.getContext().getAuthentication().getName();
        Booking booking = bookingRepo.getById(bookingId);
        if(!booking.getCustomer().getEmail().equals(email)){
            throw new RuntimeException("Unauthorized: Only the Authorized Customer  can cancel this booking.");
        }
        if(booking.getBookingStatus().equals(BookingStatus.COMPLETED) ||
                booking.getBookingStatus().equals(BookingStatus.CANCELLED) || booking.getBookingStatus().equals(BookingStatus.CANCELLED_BY_PROVIDER)
                ||booking.getBookingStatus().equals(BookingStatus.REJECTED)){
            throw new RuntimeException("Can't cancel the Booking which is already "+booking.getBookingStatus());
        }
        double estimateFee = calculateCancellationFee(booking);
        booking.setCancellationFee(estimateFee);
        bookingRepo.save(booking);
        otpService.generateBookingOtp(booking);

        return new ApiResponse(true,"OTP  fro the cancellation has been sent to the Registered Email");
 }
@Transactional
 public BookingResponseDto cancelBookingWithOtp(Long bookingId,String otp){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Booking booking=bookingRepo.findById(bookingId).orElseThrow(()-> new RuntimeException("Booking Not Found"));
        if(!booking.getCustomer().getEmail().equals(email)){
            throw new RuntimeException("Unauthorized: Only the Authorized Customer  can cancel this booking.");
        }
        String redisKey = "booking_otp:"+booking.getId();
        String hashedOtp = redisService.getValue(redisKey);
        if(hashedOtp==null){
            throw new RuntimeException("Otp Expired! please ask customer to resend the Otp");
        }
        if(!passwordEncoder.matches(otp,hashedOtp)){
            throw new RuntimeException("Invalid Otp, Please try Again Later");
        }
        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepo.save(booking);
        System.out.println("the Booking that got cancelled is the booking with id "+booking.getId());
        redisService.deleteValue(redisKey);
        notificationManager.triggerNotification(booking,BookingStatus.CANCELLED);
        return BookingResponseDtoMapper.mapDto(booking);


 }
 private double calculateCancellationFee(Booking booking){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime scheduledTime = booking.getScheduleTime();
        long minutesRemain = Duration.between(now,scheduledTime).toMinutes();
        if(minutesRemain<0){
            throw new RuntimeException("Can't cancel the Booking which is in Past ");
        }

        if(minutesRemain<120){
            double originalPrice = booking.getServiceOffering().getPrice();
            return originalPrice*0.20;

        }
        return 0.0;
 }

 public void resendOtp(Long bookingId){
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(()-> new RuntimeException("Booking Not Found"));
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isCustomer =  booking.getCustomer().getEmail().equals(currentUser);
        boolean isProvider = booking.getProvider().getUser().getEmail().equals(currentUser);

        if(!isCustomer && !isProvider){
            throw new RuntimeException("Not Authorized to manage this Booking.");
        }

        if(!booking.getBookingStatus().equals(BookingStatus.CONFIRMED)){
            throw new RuntimeException("Can't Generate Otp booking not Confirmed Yet");
        }

        // Give limit check so that user just not click send otp as mush as they want
     String rateLimitKey = "resend_cooldown:" + bookingId;
     if (redisService.getValue(rateLimitKey) != null) {
         throw new RuntimeException("Please wait 1 minute before requesting another OTP.");
     }


     redisService.saveValue(rateLimitKey, "WAIT", 1);

     otpService.generateBookingOtp(booking);

 }

    public List<BookingResponseDto> findAllBookings() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user  = userRepository.findByEmail(email)
                .orElseThrow(()->new EntityNotFoundException("User not found"));
        Set<Roles> roles = user.getRoles();

        boolean isAdmin = roles.stream()
                .anyMatch(role ->
                        role.getName().equals("ROLE_ADMIN"));
        if(!isAdmin){
            throw new  IllegalIdentifierException(" unAuthorized");
        }
        List<Booking> bookings= bookingRepo.findAll();
        if(bookings==null){
            throw new EntityNotFoundException("No booking Available ");
        }
         return bookings.stream().map(BookingResponseDtoMapper::mapDto).toList();

    }


}

