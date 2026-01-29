package com.hymer.hymarket.controller;

import com.hymer.hymarket.dto.ProviderResponseDto;
import com.hymer.hymarket.dto.ReviewRequestDto;
import com.hymer.hymarket.dto.ReviewResponseDto;
import com.hymer.hymarket.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final  ReviewService reviewService;
    @Autowired
    public ReviewController(ReviewService reviewService){
       this.reviewService= reviewService;
   }

   @PostMapping("/new/review")
    public ResponseEntity<?> WriteNewReview(@RequestBody ReviewRequestDto review){
        reviewService.writeReview(
                review.getBookingID(),
                review.getRating(),
                review.getComment()
        );
        return ResponseEntity.ok(Map.of("message","Review successfully created!"));
   }


   @GetMapping("/provider/{provider_Id}") // The Public EndPoint
   // get the reviews of the provider that can be accessible through Public EndPoint
    public ResponseEntity<List<ReviewResponseDto>> getReviews(@PathVariable Long provider_Id){
        return ResponseEntity.ok(reviewService.getProviderReviews(provider_Id));
   }

   @GetMapping("/my-reviews")
    public ResponseEntity<List<ProviderResponseDto>> myReviews(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(reviewService.getMyReviews(email));
   }





}
