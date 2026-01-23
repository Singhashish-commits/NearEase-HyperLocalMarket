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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BookingService{
    private final  BookingRepo bookingRepo;
    private final  ServiceOfferingRepo serviceOfferingRepo;
    private  final UserRepository userRepository;
    @Autowired
    public BookingService( BookingRepo bookingRepo, ServiceOfferingRepo serviceOfferingRepo, UserRepository userRepository) {
        this.bookingRepo = bookingRepo;
        this.serviceOfferingRepo = serviceOfferingRepo;
        this.userRepository = userRepository;
    }

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
        // Are use Authorized to manage this booking
//        try{
//            BookingStatus newStatusEnum = BookingStatus.valueOf(newStatus);
//            booking.setBookingStatus(newStatusEnum);
//
//        }catch(IllegalArgumentException e){
//            throw new RuntimeException("Invalid Status , Use CONFIRMED,REJECTED,or COMPLETED");
//
//        }
        booking.setBookingStatus(newStatus);
        Booking updatedBooking = bookingRepo.save(booking);
        return mapBookingDto(updatedBooking);

    }



}

