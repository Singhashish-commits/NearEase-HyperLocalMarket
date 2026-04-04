package com.hymer.hymarket.Repository;

import com.hymer.hymarket.model.PaymentTransection;
import com.razorpay.Payment;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository  extends JpaRepository<PaymentTransection, Long> {
    Optional<PaymentTransection> findByRazorPayOrderId(String razorPayOrderId, Limit limit);

    Optional<PaymentTransection> findByBookingId(Long bookingId);
}
