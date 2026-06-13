package com.hymer.hymarket.service;

import com.hymer.hymarket.Mapper.ProviderPortfolioDtoMapper;
import com.hymer.hymarket.Mapper.ServiceOfferingMapper;
import com.hymer.hymarket.Repository.*;
import com.hymer.hymarket.dto.*;
import com.hymer.hymarket.model.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final ReviewRepository reviewRepo;

    @Autowired
    public ProviderProfileService(UserRepository userRepository, ProviderProfileRepository providerRepo, ServiceTypeRepo serviceTypeRepo, ServiceOfferingRepo serviceOfferingRepo, FileUploadService fileUploadService, BookingRepo bookingRepo, ReviewRepository reviewRepo) {
        this.userRepository = userRepository;
        this.providerRepo = providerRepo;
        this.serviceTypeRepo = serviceTypeRepo;
        this.serviceOfferingRepo = serviceOfferingRepo;
        this.fileUploadService = fileUploadService;
        this.bookingRepo = bookingRepo;
        this.reviewRepo = reviewRepo;



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
        providerProfile.setCity(request.getCity());
        providerProfile.setState(request.getState());
        providerProfile.setPinCode(request.getPinCode());
        providerProfile.setLatitude(request.getLatitude());
        providerProfile.setLongitude(request.getLongitude());
        providerRepo.save(providerProfile);
                return ResponseEntity.ok(new ApiResponse(true, "Application Submitted for verification"));

    }

       @Transactional     // Add The Services
    public ResponseEntity<ApiResponse> addService(ServiceOfferingRequest serviceRequest, MultipartFile file) throws IOException{

        ProviderProfile providerProfile  = getVerifiedProviderProfile();
         if(providerProfile==null){
             return  ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Provider Profile not found"));
         }
         if(!providerProfile.isVerified()){
             return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Provider Profile is not verified"));
         }
         Long providerId = providerProfile.getId();
         Long serviceTypeId = serviceRequest.getServiceTypeId();
         boolean exist = serviceOfferingRepo.existsByProviderProfileIdAndServiceTypeId(providerId,serviceTypeId);
         if(exist){
             return ResponseEntity.badRequest().body(new ApiResponse(false,"Service Already Exist"));
         }
       ServiceType serviceType = serviceTypeRepo.findById(serviceRequest.getServiceTypeId())
                .orElseThrow(()-> new RuntimeException("Service Type not find with ID "+serviceRequest.getServiceTypeId()));

         ServiceOffering serviceOffering = new ServiceOffering();
         serviceOffering.setProviderProfile(providerProfile);
         serviceOffering.setServiceType(serviceType);
         if(serviceRequest.getPrice()<=0.0){
             return ResponseEntity.badRequest().body(new ApiResponse(false,"Price must be greater than 0"));
         }
         serviceOffering.setPrice(serviceRequest.getPrice());
         String description = serviceRequest.getDescription();
         if(description==null || description.trim().isEmpty()){
             return ResponseEntity.badRequest().body(new ApiResponse(false,"Description is required"));
         }
         serviceOffering.setDescription(description.trim());
           fileUploadService.uploadFile(file);
         serviceOfferingRepo.save(serviceOffering);
         return ResponseEntity.ok(new ApiResponse(true, "Service Added Successfully"));
    }
    public List<ProviderPortfolioDto> getProviderPortfolio(Long providerProfileId){
        // any one can see it so no need to check the user logged in or not
        List<Booking> completedWork = bookingRepo.findByServiceOfferingProviderProfileIdAndBookingStatus(providerProfileId, BookingStatus.COMPLETED);
        return completedWork.stream()
                // Only include bookings that actually have an 'After' image to show off
                .filter(booking -> booking.getAfterImages()!=null)
                .map(ProviderPortfolioDtoMapper::mapDto)
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

    public ProviderDashBoardDto getProviderDashBoard(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
         ProviderProfile provider = providerRepo.findByUser(user).orElseThrow(()-> new RuntimeException("Provider profile not found"));
         long providerId = provider.getId();
         long completedJobs = bookingRepo.countByServiceOfferingProviderProfileIdAndBookingStatus(providerId, BookingStatus.COMPLETED);
         long pendingRequest = bookingRepo.countByServiceOfferingProviderProfileIdAndBookingStatus(providerId, BookingStatus.PENDING);
         Double earnings = bookingRepo.calculateTotalEarning(providerId, BookingStatus.COMPLETED);
         Double finalEarning = (earnings!=null)? earnings:0;
        Double averageRating = providerRepo.findAverageRatingById(providerId);

        // fetching the active Services
        List<ServiceOffering> offering = serviceOfferingRepo.findByProviderProfileId(providerId);
        List<ServiceOfferingResponse> activeService = offering.stream().
        map(ServiceOfferingMapper::mapDto).toList();
        //build the DashBoardDto
        ProviderDashBoardDto providerDashBoardDto = new ProviderDashBoardDto();
        providerDashBoardDto.setProviderName(user.getFirstName()+" "+user.getLastName());
        providerDashBoardDto.setProviderEmail(email);
        providerDashBoardDto.setImageUrl(user.getProfilePictureImageUrl());
        providerDashBoardDto.setPhoneNo(user.getPhoneNumber());
        providerDashBoardDto.setTotalEarning(finalEarning);
        providerDashBoardDto.setCompletedJobs(completedJobs);
        providerDashBoardDto.setPendingRequest(pendingRequest);

        providerDashBoardDto.setAverageRating(averageRating!=null ? averageRating : 0.0);
        providerDashBoardDto.setActiveServices(activeService);
        return providerDashBoardDto;
    }


    public List<ServiceOfferingResponse> MyServices() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
        if(user.getProviderProfile()==null){
            throw new EntityNotFoundException("Provider profile not found");
        }
        List<ServiceOffering> serviceOfferings = serviceOfferingRepo.findByProviderProfileId(user.getProviderProfile().getId());
       return serviceOfferings.stream().map(ServiceOfferingMapper::mapDto).toList();

    }

    public ApiResponse editService(Long id, ServiceOfferingRequest serviceRequest,MultipartFile file) throws IOException {
       ProviderProfile profile = getVerifiedProviderProfile();
       ServiceOffering exist = serviceOfferingRepo.findById(id)
               .orElseThrow(()-> new EntityNotFoundException("Service offering not found"));
       if(profile.getId()!= exist.getProviderProfile().getId()){
           return new ApiResponse(false, "You don't have permission to edit this service.");
       }
       exist.setDescription(serviceRequest.getDescription());
       exist.setPrice(serviceRequest.getPrice());

        if(!exist.getServiceType().getId().equals(serviceRequest.getServiceTypeId())){
            ServiceType newType = serviceTypeRepo.findById(serviceRequest.getServiceTypeId())
                    .orElseThrow(()-> new
                            RuntimeException("Service Type not find with ID "+serviceRequest.getServiceTypeId()));
            exist.setServiceType(newType);
        }

        if(file!= null && !file.isEmpty()){
            String imgUrl = fileUploadService.uploadFile(file);
            exist.setImageUrl(imgUrl);
        }
        serviceOfferingRepo.save(exist);

       return new ApiResponse(true, "Successfully edited the service.");
    }




    private ProviderProfile getVerifiedProviderProfile() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProviderProfile profile = user.getProviderProfile();
        if (profile == null || !profile.isVerified()) {
            throw new RuntimeException("Provider Profile is missing or not verified");
        }
        return profile;
    }
    
}
