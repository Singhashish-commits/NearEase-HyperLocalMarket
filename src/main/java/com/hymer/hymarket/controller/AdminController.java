package com.hymer.hymarket.controller;
import com.hymer.hymarket.dto.ApiResponse;
import com.hymer.hymarket.dto.BookingResponseDto;
import com.hymer.hymarket.dto.ProviderPortfolioDto;
import com.hymer.hymarket.dto.ProviderProfileDto;
import com.hymer.hymarket.service.AdminService;
import com.hymer.hymarket.service.BookingService;
import com.hymer.hymarket.service.PaymentService;
import com.hymer.hymarket.service.ProviderProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final   AdminService adminService;
    private  final ProviderProfileService providerProfileService;
    private final BookingService bookingService;
    private final PaymentService paymentService;

    @Autowired
    public AdminController(AdminService adminService, ProviderProfileService providerProfileService, BookingService bookingService, PaymentService paymentService) {
        this.adminService = adminService;
        this.providerProfileService = providerProfileService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
    }
    @PostMapping("/provider/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> approve(@PathVariable long id){

        return adminService.approveProviderRequest(id);
    }
    @GetMapping("/provider/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProviderProfileDto>> getPendingAccount(){
        return  ResponseEntity.ok(adminService.pendingRequest());
    }

    @GetMapping("/all/bookings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponseDto>> getAllBookings(){
        return ResponseEntity.ok(bookingService.findAllBookings());
    }

    @PostMapping("/payout/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> providerPayout(@PathVariable("bookingId") Long bookingId) throws Exception {
       return ResponseEntity.ok( paymentService.providerPayout(bookingId));
    }

    @PostMapping("/refund/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> refund(@PathVariable("bookingId") Long bookingId) throws Exception {
        return ResponseEntity.ok(paymentService.processRefund(bookingId));
    }

    @PostMapping("/migrate-to-elastic")
    public ResponseEntity<ApiResponse> migrateToElastic(){
        return ResponseEntity.ok(adminService.migrateData());
    }




}
