package com.hymer.hymarket.Mapper;

import com.hymer.hymarket.dto.ReviewResponseDto;
import com.hymer.hymarket.model.Review;

public class ReviewResponseDtoMapper {
    public static ReviewResponseDto mapDto(Review review){
        if(review == null){
            return null;
        }
        ReviewResponseDto reviewResponseDto = new ReviewResponseDto();
        reviewResponseDto.setId(review.getId());
        reviewResponseDto.setCreatedAt(review.getCreatedAt());
        reviewResponseDto.setRating(review.getRating());
        reviewResponseDto.setProviderReply(review.getProviderReply());
        reviewResponseDto.setRepliedAt(review.getRepliedAt());
        reviewResponseDto.setComment(review.getComment());
        if(review.getBooking() != null && review.getBooking().getCustomer() != null && review.getBooking().getCustomer().getFirstName() != null){
            reviewResponseDto.setCustomerName(review.getBooking().getCustomer().getFirstName());
        }else{
            reviewResponseDto.setCustomerName("Verified Customer");
        }

        return reviewResponseDto;

    }

}
