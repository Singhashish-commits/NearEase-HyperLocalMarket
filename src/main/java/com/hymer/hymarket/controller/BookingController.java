package com.hymer.hymarket.controller;

import com.hymer.hymarket.dto.BookingRequestDto;
import com.hymer.hymarket.dto.BookingResponseDto;
import com.hymer.hymarket.model.BookingStatus;
import com.hymer.hymarket.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    @PutMapping("/{bookingId}/status")
    public ResponseEntity<BookingResponseDto> updateBooking(@PathVariable Long bookingId, @RequestParam BookingStatus status){
            BookingResponseDto updatedBooking = bookingService.updateBookingStatus(bookingId, status);
            return ResponseEntity.ok(updatedBooking);

    }

    @PutMapping(value = "/{bookingId}/complete",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookingResponseDto> completeBooking(
            @PathVariable Long bookingId,
            @RequestParam String otp,
            @RequestPart(value = "beforeImages",required = false)MultipartFile beforeImage,
            @RequestPart(value = "afterImages", required =false) MultipartFile afterImage
            ) throws IOException {

        BookingResponseDto response = bookingService.completeBookingWithOtp(bookingId, otp,beforeImage,afterImage);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/resend-otp")
    public ResponseEntity<?> resendOtp(@PathVariable Long id){
        bookingService.resendOtp(id);
        return ResponseEntity.ok(Map.of("message", "O New Otp Sent to the Customer email"));
    }










}
