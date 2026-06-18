package com.hymer.hymarket.Repository;

import com.hymer.hymarket.model.Booking;
import com.hymer.hymarket.model.PaymentStatus;
import com.hymer.hymarket.model.PaymentTransection;
import com.razorpay.Payment;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository  extends JpaRepository<PaymentTransection, Long> {
    Optional<PaymentTransection> findByRazorPayOrderId(String razorPayOrderId, Limit limit);

//    Optional<PaymentTransection> findByBookingId(Long bookingId);

//    Optional<PaymentTransection> findByBooking(Booking booking);


    Optional<PaymentTransection> findFirstByBookingIdAndPaymentStatusIn(Long bookingId, List<PaymentStatus> statuses);

    Optional<PaymentTransection> findFirstByBookingAndPaymentStatusIn(Booking booking, List<PaymentStatus> statuses);




}


