package com.hymer.hymarket.service;

import com.hymer.hymarket.Repository.BookingRepo;
import com.hymer.hymarket.Repository.ServiceOfferingRepo;
import com.hymer.hymarket.Repository.UserRepository;
import com.hymer.hymarket.dto.BookingRequestDto;
import com.hymer.hymarket.dto.BookingResponseDto;
import com.hymer.hymarket.dto.ProviderProfileDto;
import com.hymer.hymarket.dto.UserProfileDto;
import com.hymer.hymarket.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BookingService{
    private final  BookingRepo bookingRepo;
    private final  ServiceOfferingRepo serviceOfferingRepo;
    private  final UserRepository userRepository;
    private final MailService mailService;
    private final RedisService redisService;

    @Autowired
    public BookingService(BookingRepo bookingRepo, ServiceOfferingRepo serviceOfferingRepo, UserRepository userRepository, MailService mailService, RedisService redisService) {
        this.bookingRepo = bookingRepo;
        this.serviceOfferingRepo = serviceOfferingRepo;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.redisService = redisService;
    }

    // For the Otp generation used in generateAndSendOtp method  here
    private static final SecureRandom secureRandom = new SecureRandom();


    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) {
        // get the current logged-in user
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
        Booking SavedBooking = bookingRepo.save(booking);

        return mapBookingDto(SavedBooking);
    }

    private BookingResponseDto mapBookingDto(Booking booking) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
        bookingResponseDto.setBookingStatus(booking.getBookingStatus());
        bookingResponseDto.setBookingTime(booking.getBookingTime());
        bookingResponseDto.setScheduledTime(booking.getScheduleTime());
        bookingResponseDto.setWorkLocation(booking.getWorkLocation());
        bookingResponseDto.setCostumerRequest(booking.getCustomerRequest());
        // Mapping the Service Info
        bookingResponseDto.setServiceName(booking.getServiceOffering().getServiceType().getName());
        bookingResponseDto.setPrice(booking.getServiceOffering().getPrice());

        // Mapping the Customer
        User customer = booking.getCustomer();
      if(customer != null){
          UserProfileDto userProfile = new UserProfileDto();
          userProfile.setId(customer.getId());
          userProfile.setFirstName(customer.getFirstName());
          userProfile.setLastName(customer.getLastName());
          userProfile.setEmail(customer.getEmail());
          userProfile.setPhone(customer.getPhoneNumber());
          //Mapping the user who booked to teh dto of the booking Response
          bookingResponseDto.setCustomer(userProfile);
      }
      else{
          System.out.println(" booking id "+ booking.getId()+" has no customer ");
      }

        // Map the provider Profile
        ProviderProfileDto providerProfileDto = getProviderProfileDto(booking);

        bookingResponseDto.setProvider(providerProfileDto);

        return bookingResponseDto;
    }

    private static ProviderProfileDto getProviderProfileDto(Booking booking) {
        ProviderProfile providerProfile  = booking.getProvider();
        ProviderProfileDto providerProfileDto = new ProviderProfileDto();
        providerProfileDto.setId(providerProfile.getId());
        providerProfileDto.setBio(providerProfile.getBio());
        providerProfileDto.setExperience(providerProfile.getExperience());
        providerProfileDto.setSkill(providerProfile.getSkills());
        providerProfileDto.setAddress(providerProfile.getAddress());
        providerProfileDto.setVerified(providerProfile.isVerified());
        // user Account of the Provider who is providing the Service
        User providerUser = booking.getProvider().getUser();
        UserProfileDto providerUserProfile = new UserProfileDto();
        providerUserProfile.setId(providerUser.getId());
        providerUserProfile.setFirstName(providerUser.getFirstName());
        providerUserProfile.setLastName(providerUser.getLastName());
        providerUserProfile.setPhone(providerUser.getPhoneNumber());
        providerUserProfile.setEmail(providerUser.getEmail());
        providerProfileDto.setUser(providerUserProfile);
        return providerProfileDto;
    }

    public List<BookingResponseDto> customerBookings(){
        // the list of booking that the customer is booking
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow( ()->new RuntimeException("User not Found"));
        List<Booking>bookings = bookingRepo.findByCustomerId(user.getId());

            return bookings.stream()
                    .map(this ::mapBookingDto)
                    .collect(Collectors.toList());
    }

    public List<BookingResponseDto> providerBookings(){
        // booking that the provider got from the customer
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException(" User Not Found"));
        if(user.getProviderProfile()==null) throw new RuntimeException("Provider Profile Not Found");
        List<Booking> bookings = bookingRepo.findByProviderId(user.getProviderProfile().getId());
        return bookings.stream()
                    .map(this ::mapBookingDto)
                    .collect(Collectors.toList());

    }

    public BookingResponseDto updateBookingStatus(Long bookingId, BookingStatus newStatus){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(()-> new RuntimeException("Booking not found "));
       if(!booking.getProvider().getUser().getId().equals(currentUser.getId())){
           throw new RuntimeException("Not Authorized to manage this Booking");
       }
        booking.setBookingStatus(newStatus);
        Booking updatedBooking = bookingRepo.save(booking);
        return mapBookingDto(updatedBooking);

    }
 private void generateAndSendOtp(Booking booking){
    int optValue = 1000+secureRandom.nextInt(9000);
    String otp = String.valueOf(optValue);
    String redisKey = "booking_otp: " + booking.getId();
    redisService.saveValue(redisKey,otp,10);

    String subject = "Yor Service Otp (Valid for 10 minutes)";
    String body = "Give This code to the Provider to Mark booking Confirmed";
    mailService.sendMail(booking.getCustomer().getEmail(),subject,body);

 }

 public BookingResponseDto completeBookingWithOtp(Long bookingId,String otp){
        Booking booking=bookingRepo.findById(bookingId)
                .orElseThrow(()-> new RuntimeException("Booking Not Found"));
     String LoggedInProvider =SecurityContextHolder.getContext().getAuthentication().getName();
     String currProvider = booking.getProvider().getUser().getEmail();
     if(!currProvider.equals(LoggedInProvider)){
         throw new RuntimeException("Unauthorized: Only the assigned provider can complete this booking.\"");
     }
        if(booking.getBookingStatus().equals(BookingStatus.CONFIRMED)){
            throw new RuntimeException("Booking is not Ready for Completion");
        }


        String redisKey = "Booking_otp: "+booking.getId();
        String checkedOtp = redisService.getValue(redisKey);
        if(checkedOtp==null){
            throw new RuntimeException("Otp Expired ! please ask customer to resend  the Otp");
        }
        if(!checkedOtp.equals(otp)){
            throw new RuntimeException("Invalid Otp, Please try Again Later");
        }

        // if everything goes good booking marked completed;
     booking.setBookingStatus(BookingStatus.COMPLETED);
        redisService.deleteValue(redisKey);
        return mapBookingDto(bookingRepo.save(booking));
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

      // Set a 1-minute cooldown flag
     redisService.saveValue(rateLimitKey, "WAIT", 1);

        generateAndSendOtp(booking);
        System.out.println("Otp");

 }







}

