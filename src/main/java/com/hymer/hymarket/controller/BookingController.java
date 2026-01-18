package com.hymer.hymarket.controller;

import com.hymer.hymarket.dto.BookingRequestDto;
import com.hymer.hymarket.dto.BookingResponseDto;
import com.hymer.hymarket.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/bookings/")
public class BookingController {
    private  final BookingService bookingService;
    @Autowired
    public BookingController(BookingService bookingService){
        this.bookingService = bookingService;
    }

    @PostMapping("/bookService")
    public ResponseEntity<?> bookService(@RequestBody BookingRequestDto bookingRequestDto){
        try{
            BookingResponseDto booking = bookingService.createBooking(bookingRequestDto);
            return ResponseEntity.ok().body(booking);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all-bookings")
    public ResponseEntity<List<BookingResponseDto>> getMyBookings(){
        try{
            return ResponseEntity.ok(bookingService.customerBookings());
        }catch(Exception e){
            return ResponseEntity.badRequest().body(Collections.emptyList());
    }
    }
    @GetMapping("/booking-requests")
    public ResponseEntity<?> getBookingRequests(){
        try{
            return ResponseEntity.ok(bookingService.providerBookings());
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage() +"Error is Happening here ");
        }
    }

    @PostMapping("/{bookingId}/status")
    public ResponseEntity<?> updateBooking(@PathVariable Long bookingId, @RequestParam String status){
        try{
            BookingResponseDto updatedBooking = bookingService.updateBookingStatus(bookingId, status);
            return ResponseEntity.ok(updatedBooking);
        }catch(Exception e){
            return  ResponseEntity.badRequest().body(e.getMessage());
        }

    }










}
