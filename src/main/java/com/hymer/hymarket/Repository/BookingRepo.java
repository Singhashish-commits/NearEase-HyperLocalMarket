package com.hymer.hymarket.Repository;

import com.hymer.hymarket.model.Booking;
import com.hymer.hymarket.model.BookingStatus;
import com.hymer.hymarket.model.PaymentStatus;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepo extends JpaRepository<Booking, Long> {
   List<Booking> findByCustomerId(long customerId);
   List<Booking> findByProviderId(long providerId);
   List<Booking> findByServiceOfferingProviderProfileIdAndBookingStatus(Long providerId, BookingStatus status);

    @Query("SELECT SUM(b.price) FROM Booking b" +
            " WHERE b.provider.id = :providerId AND b.paymentStatus = :paymentStatus")
    Double calculateTotalEarning(@Param("providerId") Long providerId, @Param("paymentStatus") PaymentStatus paymentStatus);


    long countByServiceOfferingProviderProfileIdAndBookingStatus(long providerId, BookingStatus bookingStatus);
}
