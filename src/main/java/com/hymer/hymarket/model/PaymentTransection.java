package com.hymer.hymarket.model;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class PaymentTransection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name ="bookingId",nullable = false)
    private Booking booking;

    private String razorPayOrderId;
    private String razorPayPaymentId;
    private String razorPaySignature;
    private Integer amount;
    private String currency="INR";
    private String paymentMethod;
    private String status;
    private PaymentStatus paymentStatus;
    @CreationTimestamp
    @Column( updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;





}
