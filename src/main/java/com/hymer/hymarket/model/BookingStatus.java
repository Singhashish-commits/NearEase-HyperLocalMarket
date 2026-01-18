package com.hymer.hymarket.model;

public enum BookingStatus {
    PENDING,    // User requested, Provider hasn't seen it yet
    CONFIRMED,  // Provider accepted
    REJECTED,   // Provider said no
    COMPLETED,  // Work done
    CANCELLED
}

