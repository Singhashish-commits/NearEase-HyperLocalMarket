package com.hymer.hymarket.service;

import com.hymer.hymarket.Repository.BookingRepo;
import com.hymer.hymarket.Repository.PaymentRepository;
import com.hymer.hymarket.dto.PaymentResponseDto;
import com.hymer.hymarket.model.Booking;
import com.hymer.hymarket.model.BookingStatus;
import com.hymer.hymarket.model.PaymentTransection;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final BookingRepo bookingRepo;
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(BookingRepo bookingRepo, PaymentRepository paymentRepository) {
        this.bookingRepo = bookingRepo;
        this.paymentRepository = paymentRepository;
    }

    @Value("${razorpay.key.id}")
    private String razorPayKey;

    @Value("${razorpay.key.secret}")
    private String razorPaySecret;

    public PaymentResponseDto createOrder(Long bookingId) throws Exception{
        Booking booking = bookingRepo.findById(bookingId).orElseThrow(()->new RuntimeException("Booking not found"));

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!booking.getCustomer().getEmail().equals(userEmail)){
            throw new RuntimeException("Unauthorized to pay for this booking");
        }
        if(booking.getBookingStatus()!= BookingStatus.CONFIRMED){
            throw new RuntimeException("Booking is Not confirmed yet Payment can be done Only After order gets Confirmed");
        }
        RazorpayClient razorpayClient = new RazorpayClient(razorPayKey,razorPaySecret);
        double actualPrice = booking.getServiceOffering().getPrice();
        int FinalPrice = (int)Math.round(actualPrice * 100); // RazorPay accepts amount in small Amount
        JSONObject paymentRequest = new JSONObject();
        paymentRequest.put("amount",FinalPrice);
        paymentRequest.put("currency","INR");
        paymentRequest.put("receipt", "txn_booking_" + bookingId);

        Order razorPayOrder = razorpayClient.orders.create(paymentRequest);
        String orderId = razorPayOrder.get("id");
        PaymentTransection transection = new PaymentTransection();
        transection.setBooking(booking);
        transection.setRazorPayOrderId(orderId);
        transection.setAmount(FinalPrice);
        transection.setCurrency("INR");
        transection.setStatus("CREATED");
        paymentRepository.save(transection);


        booking.setTransectionId(orderId);
        bookingRepo.save(booking);
        PaymentResponseDto response = new PaymentResponseDto();
        response.setRazorpayOrderId(orderId);
        response.setAmountInPaise(FinalPrice);
        response.setCurrency("INR");
        response.setCustomerEmail(booking.getCustomer().getFirstName()+" "+booking.getCustomer().getLastName());
        response.setCustomerEmail(booking.getCustomer().getEmail());
        response.setCustomerPhone(booking.getCustomer().getPhoneNumber());

        return  response;

    }


}
