package com.hymer.hymarket.service;

import com.hymer.hymarket.Repository.*;
import com.hymer.hymarket.dto.*;
import com.hymer.hymarket.model.*;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProviderProfileService {
    private final ServiceTypeRepo serviceTypeRepo;
    private final ServiceOfferingRepo serviceOfferingRepo;
    private final UserRepository userRepository;
    private  final ProviderProfileRepository providerRepo;
    private final FileUploadService fileUploadService;
    private final BookingRepo bookingRepo;
//    private final Filter filter;

    @Autowired
    public ProviderProfileService(UserRepository userRepository, ProviderProfileRepository providerRepo, ServiceTypeRepo serviceTypeRepo, ServiceOfferingRepo serviceOfferingRepo, FileUploadService fileUploadService, BookingRepo bookingRepo) {
        this.userRepository = userRepository;
        this.providerRepo = providerRepo;
        this.serviceTypeRepo = serviceTypeRepo;
        this.serviceOfferingRepo = serviceOfferingRepo;
        this.fileUploadService = fileUploadService;
        this.bookingRepo = bookingRepo;



    }

    // apply to be Provider
    public ResponseEntity<ApiResponse> apply(@RequestBody ProviderProfileRequest request){
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        String email =  userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));

        // Already Applied
        if(user.getProviderProfile()!= null){
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Provider Profile already exists"));
        }
        ProviderProfile providerProfile = new ProviderProfile();
        providerProfile.setUser(user);
        providerProfile.setBio(request.getBio());
        providerProfile.setSkills(request.getSkills());
        providerProfile.setExperience(request.getExperience());
        providerProfile.setVerified(false);
        providerProfile.setAddress(request.getAddress());
        providerRepo.save(providerProfile);
                return ResponseEntity.ok(new ApiResponse(true, "Application Submitted for verification"));

    }
            // Add The Services
    public ResponseEntity<ApiResponse> addService(ServiceOfferingRequest serviceRequest, MultipartFile file) throws Exception{
        //gets the currently logged-in user
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        User user =  userRepository.findByEmail(userDetails.getUsername()).orElseThrow(()-> new RuntimeException("User not found"));
         // get their provider profile
        ProviderProfile providerProfile  = user.getProviderProfile();
         if(providerProfile==null){
             return  ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Provider Profile not found"));
         }
         if(!providerProfile.isVerified()){
             return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Provider Profile is not verified"));
         }
       ServiceType serviceType = serviceTypeRepo.findById(serviceRequest.getServiceTypeId())
                .orElseThrow(()-> new RuntimeException("Service Type not find with ID "+serviceRequest.getServiceTypeId()));

         ServiceOffering serviceOffering = new ServiceOffering();
         serviceOffering.setProviderProfile(providerProfile);
         serviceOffering.setServiceType(serviceType);
         serviceOffering.setPrice(serviceRequest.getPrice());
         serviceOffering.setDescription(serviceRequest.getDescription());
         if(file!=null&& !file.isEmpty()){
             String imageUrl = fileUploadService.uploadFile(file);
             serviceOffering.setImageUrl(imageUrl);
         }
         serviceOfferingRepo.save(serviceOffering);

         return ResponseEntity.ok(new ApiResponse(true, "Service Added Successfully"));

    }


    public List<ProviderPortfolioDto> getProviderPortfolio(Long providerProfileId){
        // any one can see it so no need to check the user logged in or not
        List<Booking> completedWork = bookingRepo.findByServiceOfferingProviderProfileIdAndBookingStatus(providerProfileId, BookingStatus.COMPLETED);
        return completedWork.stream()
                // Only include bookings that actually have an 'After' image to show off
                .filter(booking -> booking.getAfterImages()!=null)
                .map(this::mapToPortfolioDto)
                .collect(Collectors.toList());
    }
    public List<ProviderPortfolioDto>getMyPortfolio(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
        ProviderProfile providerProfile = providerRepo.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Provider profile not found"));
        return getProviderPortfolio(providerProfile.getId());
    }


    public void removePortfolioImages(Long bookingId){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
        ProviderProfile provider = providerRepo.findByUser(user).orElseThrow(()-> new RuntimeException("Provider profile not found"));
        Booking booking = bookingRepo.findById(bookingId).orElseThrow(()-> new RuntimeException("Booking not found"));
        if (!booking.getServiceOffering().getProviderProfile().getId().equals(provider.getId())) {
            throw new RuntimeException("Unauthorized: You do not own this booking.");
        }
        booking.setBeforeImages(null);
        booking.setAfterImages(null);
        bookingRepo.save(booking);
    }


    private ProviderPortfolioDto mapToPortfolioDto(Booking booking){
        ProviderPortfolioDto dto = new ProviderPortfolioDto();
        dto.setBookingId(booking.getId());
        dto.setServiceName(booking.getServiceOffering().getServiceType().getName());
        dto.setCategory(booking.getServiceOffering().getServiceType().getCategory().getName());
        dto.setAfterImageUrl(booking.getAfterImages());
        dto.setBeforeImageUrl(booking.getBeforeImages());
        return dto;
    }
}
