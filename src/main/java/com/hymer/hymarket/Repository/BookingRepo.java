package com.hymer.hymarket.Repository;

import com.hymer.hymarket.model.Booking;
import com.hymer.hymarket.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepo extends JpaRepository<Booking, Long> {
   List<Booking> findByCustomerId(long customerId);
   List<Booking> findByProviderId(long providerId);
   List<Booking> findByServiceOfferingProviderProfileIdAndBookingStatus(Long providerId, BookingStatus status);


}
