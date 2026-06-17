package com.hymer.hymarket.controller;

import com.hymer.hymarket.dto.ApiResponse;
import com.hymer.hymarket.dto.PaymentResponseDto;
import com.hymer.hymarket.dto.PaymentVerificationDto;
import com.hymer.hymarket.model.Booking;
import com.hymer.hymarket.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;
    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order/{bookingId}")
        public ResponseEntity<PaymentResponseDto> createPayment(@PathVariable("bookingId") Long bookingId) throws Exception {
           PaymentResponseDto responseDto= paymentService.createOrder(bookingId);
            return ResponseEntity.ok(responseDto);
        }


    @PostMapping("/mock-success/{bookingId}")
    public ResponseEntity<ApiResponse> mockPaymentSuccess(
            @PathVariable Long bookingId) {

        try {
            paymentService.mockPaymentSuccess(bookingId);
            return ResponseEntity.ok(
                    new ApiResponse(true, "Mock payment completed successfully"));

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerificationDto verificationDto) {
        try {
            boolean isSuccess = paymentService.verifyAndCompletePayment(verificationDto);
            if (isSuccess) {
                return ResponseEntity.ok("Payment verified and booking finalized successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed.");
            }
        } catch (RuntimeException e) {
            // Catches invalid signature errors
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while verifying payment: " + e.getMessage());
        }
    }


}
