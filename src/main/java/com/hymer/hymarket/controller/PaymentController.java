package com.hymer.hymarket.controller;

import com.hymer.hymarket.dto.PaymentResponseDto;
import com.hymer.hymarket.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
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
            paymentService.createOrder(bookingId);
            return ResponseEntity.ok().body(new PaymentResponseDto());
        }
}
