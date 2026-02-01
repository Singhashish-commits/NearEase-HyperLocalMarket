package com.hymer.hymarket.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity

public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="customer_id",nullable = false) // who is booking
    private User customer;



    @ManyToOne
    @JoinColumn(name = "service_offering_id", nullable = false) // what are Booking
    private ServiceOffering serviceOffering;

    @ManyToOne
    @JoinColumn(name="provider-id") // who is providing
    private ProviderProfile provider;
    private LocalDateTime scheduleTime;// when they want service
    private String workLocation; // where
    private String customerRequest;
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;
    private LocalDateTime bookingTime;

    @PrePersist
    private void onCreate(){
        this.bookingTime = LocalDateTime.now();
        if(this.bookingStatus == null){
            this.bookingStatus = BookingStatus.PENDING;
        }

    }
}
