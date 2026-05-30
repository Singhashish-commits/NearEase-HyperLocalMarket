package com.hymer.hymarket.controller;

import com.hymer.hymarket.dto.ApiResponse;
import com.hymer.hymarket.dto.PaymentResponseDto;
import com.hymer.hymarket.model.Booking;
import com.hymer.hymarket.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
      @PostMapping("/refund/{bookingId}")
    public ResponseEntity<ApiResponse> refund(@PathVariable("bookingId") Long bookingId) throws Exception {
            paymentService.processRefund(bookingId);
            return new ResponseEntity<>(new ApiResponse(true,"Refund successful!"), HttpStatus.OK);
      }

      @PostMapping("payout/{bookingId}")
    public ResponseEntity<ApiResponse> providerPayout(@PathVariable("bookingId") Long bookingId) throws Exception {
        paymentService.providerPayout(bookingId);
        return new ResponseEntity<>(new ApiResponse(true,"Payout successful!"), HttpStatus.OK);
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


}
