package com.hymer.hymarket.Repository;

import com.hymer.hymarket.dto.ProviderResponseDto;
import com.hymer.hymarket.dto.ReviewResponseDto;
import com.hymer.hymarket.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByBookingId(Long bookingId);
    List<Review> findByBooking_Provider_Id(Long bookingProviderId);
}
