package com.hymer.hymarket.service;
import com.hymer.hymarket.Repository.BookingRepo;
import com.hymer.hymarket.Repository.ProviderProfileRepository;
import com.hymer.hymarket.Repository.ReviewRepository;
import com.hymer.hymarket.Repository.UserRepository;
import com.hymer.hymarket.dto.ProviderResponseDto;
import com.hymer.hymarket.dto.ReviewResponseDto;
import com.hymer.hymarket.model.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final BookingRepo bookingRepo;


    @Autowired
    public ReviewService(ReviewRepository reviewRepository, BookingService bookingService, UserRepository userRepository, ProviderProfileRepository providerProfileRepository, BookingRepo bookingRepo) {
        this.reviewRepository = reviewRepository;
        this.bookingService = bookingService;
        this.userRepository = userRepository;
        this.providerProfileRepository = providerProfileRepository;
        this.bookingRepo = bookingRepo;
    }

    @Transactional
    public Review writeReview(Long bookingId,Integer rating, String comment){
        // Validate User before Proceeding
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));

            //Validate Booking
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(()-> new RuntimeException("Booking not found"));

        //RULE: Only the customer who booked it can review it
        if(!booking.getCustomer().getId().equals(currentUser.getId())){
            throw new RuntimeException("You are not Authorized to write review");
        }

        //can review only Completed Booking
        if(booking.getBookingStatus()!= BookingStatus.COMPLETED){
            throw new RuntimeException("You can Only review Completed services");
        }
        // One review Per Booking
        if(reviewRepository.existsByBookingId(bookingId)){
            throw new RuntimeException("Review already exists");
        }

        Review review = new Review();
        review.setBooking(booking);
        review.setRating(rating);
        review.setComment(comment);
        Review savedReview = reviewRepository.save(review);
        updateProviderStats(booking.getProvider(),rating);
        System.out.println("Review saved");
        return savedReview;

    }

    private void updateProviderStats( ProviderProfile provider, Integer newRating){
        Double currentTotal = provider.getAverageRating()* provider.getReviewCount();
        int newCount = provider.getReviewCount()+1;
        Double newAverage = (currentTotal + newRating) / newCount;
        provider.setReviewCount(newCount);
        provider.setAverageRating(newAverage);
        providerProfileRepository.save(provider);
    }


    // This is Public Api here everyOne will get the review of the Provider thet want to Book for the Service.
   //    So there is now Security Check
    public List<ReviewResponseDto> getProviderReviews(Long ProviderId){
        List<Review> reviews = reviewRepository.findByBooking_Provider_Id(ProviderId);
        return reviews.stream().map(review -> {
            ReviewResponseDto dto = new ReviewResponseDto();
            dto.setId(review.getId());
            dto.setRating(review.getRating());
            dto.setComment(review.getComment());
            dto.setCreatedAt(review.getCreatedAt());

            // Get Customer Name safely
            if (review.getBooking().getCustomer() != null) {
                dto.setCustomerName(review.getBooking().getCustomer().getFirstName());
            } else {
                dto.setCustomerName("Anonymous");
            }
            return dto;
        }).collect(Collectors.toList());

    }

    public List<ProviderResponseDto> getMyReviews(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));
        System.out.println("User Found The The is "+user.getId());

        if(user.getProviderProfile()==null){
            throw new RuntimeException("You are not Provider ,Apply to be Provider To See Your Reviews");
        }
        System.out.println("Provider Profile Found The is "+user.getProviderProfile().getId());
//        return reviewRepository.findByBooking_Provider_Id(user.getProviderProfile().getId());
        Long providerId = user.getProviderProfile().getId();
        List<Review> reviews = reviewRepository.findByBooking_Provider_Id(providerId);
        return reviews.stream().map(
                review ->  {
                    ProviderResponseDto dto = new ProviderResponseDto();
                    dto.setId(review.getId());
                    dto.setRating(review.getRating());
                    dto.setComment(review.getComment());

                    Booking booking = review.getBooking();
                    dto.setBookingId(booking.getId());
                    dto.setBookingDate(booking.getBookingTime());
                    if(booking.getCustomer()!=null){
                        dto.setCustomerName(booking.getCustomer().getFirstName()+" " + booking.getCustomer().getLastName());
                    }
                    else{
                        dto.setCustomerName("Anonymous");
                    }
                    if(booking.getProvider()!=null){
                        dto.setServiceName(booking.getServiceOffering().getServiceType().getName());
                    }
                        return dto;
                }).collect(Collectors.toList());


    }
}
