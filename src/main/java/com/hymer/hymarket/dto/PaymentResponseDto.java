package com.hymer.hymarket.dto;

import lombok.Data;

@Data
public class PaymentResponseDto {
    private String razorpayOrderId;
    private Integer amountInPaise;
    private String currency;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
}
