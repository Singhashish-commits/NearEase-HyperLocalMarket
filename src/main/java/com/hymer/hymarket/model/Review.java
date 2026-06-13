package com.hymer.hymarket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name ="booking_id", nullable = false)
    private Booking booking;
    private Integer rating;
    @Column(length = 1000)
    private String comment;
    private LocalDateTime createdAt;

    @Column(length = 1000) // It is a good practice to limit the reply length in the DB
    private String providerReply;

    private LocalDateTime repliedAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

}
