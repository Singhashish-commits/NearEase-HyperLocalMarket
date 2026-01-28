package com.hymer.hymarket.controller;

import com.hymer.hymarket.dto.BookingRequestDto;
import com.hymer.hymarket.dto.BookingResponseDto;
import com.hymer.hymarket.model.BookingStatus;
import com.hymer.hymarket.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private  final BookingService bookingService;
    @Autowired
    public BookingController(BookingService bookingService){
        this.bookingService = bookingService;
    }

    @PostMapping("/bookService")
    public ResponseEntity<?> bookService(@RequestBody BookingRequestDto bookingRequestDto){

            BookingResponseDto booking = bookingService.createBooking(bookingRequestDto);
            return ResponseEntity.ok().body(booking);
    }

    @GetMapping("/all-bookings")
    public ResponseEntity<List<BookingResponseDto>> getMyBookings(){

            return ResponseEntity.ok(bookingService.customerBookings());

    }
    @GetMapping("/booking-requests")
    public ResponseEntity<List<BookingResponseDto>> getBookingRequests(){

            return ResponseEntity.ok(bookingService.providerBookings());
    }

    @PostMapping("/{bookingId}/status")
    public ResponseEntity<?> updateBooking(@PathVariable Long bookingId, @RequestParam BookingStatus status){
            BookingResponseDto updatedBooking = bookingService.updateBookingStatus(bookingId, status);
            return ResponseEntity.ok(updatedBooking);

    }










}
