package com.hymer.hymarket.service;

import com.hymer.hymarket.Repository.BookingRepo;
import com.hymer.hymarket.Repository.PaymentRepository;
import com.hymer.hymarket.dto.PaymentResponseDto;
import com.hymer.hymarket.dto.PaymentVerificationDto;
import com.hymer.hymarket.model.Booking;
import com.hymer.hymarket.model.BookingStatus;
import com.hymer.hymarket.model.PaymentStatus;
import com.hymer.hymarket.model.PaymentTransection;
import com.razorpay.*;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final BookingRepo bookingRepo;
    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(BookingRepo bookingRepo, PaymentRepository paymentRepository) {
        this.bookingRepo = bookingRepo;
        this.paymentRepository = paymentRepository;
    }
    private RazorpayClient razorpayClient;

    @Value("${razorpay.key.id}")
    private String razorPayKey;

    @Value("${razorpay.key.secret}")
    private String razorPaySecret;

    @PostConstruct
    public void init() throws RazorpayException {
        this.razorpayClient = new RazorpayClient(razorPayKey, razorPaySecret);
    }

    @Transactional
    public PaymentResponseDto createOrder(Long bookingId) throws Exception {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!booking.getCustomer().getEmail().equals(userEmail)) {
            throw new RuntimeException("Unauthorized to pay for this booking");
        }

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED
                && booking.getBookingStatus() != BookingStatus.COMPLETED) {
            throw new RuntimeException("Booking is not confirmed yet. Payment can be done only after the order is confirmed.");
        }

//        Optional<PaymentTransection> existingTxnOpt = paymentRepository.findByBookingId(bookingId);
        Optional<PaymentTransection> existingTxnOpt = paymentRepository.findFirstByBookingIdAndPaymentStatusIn(
                bookingId, List.of(PaymentStatus.UNPAID, PaymentStatus.PAID_TO_PLATFORM, PaymentStatus.FAILED));

        if (existingTxnOpt.isPresent()) {
            PaymentTransection existingTxn = existingTxnOpt.get();

            if (existingTxn.getPaymentStatus() == PaymentStatus.UNPAID) {
                PaymentResponseDto response = new PaymentResponseDto();
                response.setRazorpayOrderId(existingTxn.getRazorPayOrderId());
                response.setAmountInPaise(existingTxn.getAmount());
                response.setCurrency(existingTxn.getCurrency());
                response.setCustomerName(booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName());
                response.setCustomerEmail(booking.getCustomer().getEmail());
                response.setCustomerPhone(booking.getCustomer().getPhoneNumber());

                return response;
            }else if (existingTxn.getPaymentStatus() == PaymentStatus.FAILED) {
                // fall through and create a fresh order below
            }
            else {
                throw new RuntimeException("Payment has already been processed or initiated for this booking.");
            }
        }

        double actualPrice = booking.getPrice();
        int finalPrice = (int) Math.round(actualPrice * 100);

        JSONObject paymentRequest = new JSONObject();
        paymentRequest.put("amount", finalPrice);
        paymentRequest.put("currency", "INR");
        paymentRequest.put("receipt", "txn_booking_" + bookingId);

        Order razorPayOrder = razorpayClient.orders.create(paymentRequest);
        String orderId = razorPayOrder.get("id");

        PaymentTransection transaction = new PaymentTransection();
        transaction.setBooking(booking);
        transaction.setRazorPayOrderId(orderId);
        transaction.setAmount(finalPrice);
        transaction.setCurrency("INR");
        transaction.setStatus("CREATED");
        transaction.setPaymentStatus(PaymentStatus.UNPAID);
        paymentRepository.save(transaction);

        booking.setTransectionId(orderId);
        bookingRepo.save(booking);

        PaymentResponseDto response = new PaymentResponseDto();
        response.setRazorpayOrderId(orderId);
        response.setAmountInPaise(finalPrice);
        response.setCurrency("INR");
        response.setCustomerName(booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName());
        response.setCustomerEmail(booking.getCustomer().getEmail());
        response.setCustomerPhone(booking.getCustomer().getPhoneNumber());
        response.setRazorPayKey(razorPayKey);
        response.setBookingId(bookingId);
        response.setDescription(booking.getServiceOffering().getDescription());

        return response;
    }


    @Transactional
    public void processRefund(Long bookingId) throws Exception{
       Booking booking= bookingRepo.findById(bookingId).orElseThrow(()->new RuntimeException("Booking not found"));

        if(booking.getPaymentStatus()!= PaymentStatus.PAID_TO_PLATFORM){
            throw new RuntimeException(
                    "Cannot process refund: payment status is " + booking.getPaymentStatus()
                            + ". Expected PAID_TO_PLATFORM.");
        }
        if (booking.getBookingStatus() != BookingStatus.CANCELLED
                && booking.getBookingStatus() != BookingStatus.CANCELLED_BY_PROVIDER) {
            throw new RuntimeException("Refund is only applicable for cancelled bookings. Current status: " + booking.getBookingStatus());
        }
        double actualPrice = booking.getPrice();
        double platformFee = booking.getPlatformCommission();
        double refundAmount = actualPrice - platformFee;
        PaymentTransection refundTxn = new PaymentTransection();
        refundTxn.setBooking(booking);
        refundTxn.setAmount((int)Math.round(refundAmount*100));
        refundTxn.setCurrency("INR");
        refundTxn.setPaymentStatus(PaymentStatus.REFUNDED);
        if(platformFee==0){
            refundTxn.setStatus("Full Amount will be Processed");
        }
        else{
            refundTxn.setStatus("platform fee will be deducted from the Total Amount");
        }
        paymentRepository.save(refundTxn);
        booking.setPaymentStatus(PaymentStatus.REFUNDED);
        bookingRepo.save(booking);


    }

    @Transactional
    public void providerPayout(Long bookingId) throws Exception{

        Booking booking = bookingRepo.findById(bookingId).orElseThrow(()->new RuntimeException("Booking not found"));
        if (booking.getBookingStatus() != BookingStatus.COMPLETED) {
            throw new RuntimeException("Cannot release payout: booking is not marked completed yet.");
        }
        if (booking.getPaymentStatus() != PaymentStatus.PAID_TO_PLATFORM) {
            throw new RuntimeException("Can't process payout: funds are not in escrow.");
        }
        double actualPrice = booking.getPrice();
        double platformFee = booking.getPlatformCommission();
        double payableAmount = actualPrice - platformFee;
        PaymentTransection paymentTxn = new PaymentTransection();
        paymentTxn.setBooking(booking);
        paymentTxn.setAmount((int)Math.round(payableAmount * 100));
        paymentTxn.setCurrency("INR");
        paymentTxn.setPaymentStatus(PaymentStatus.TRANSFER_TO_PROVIDER);
        paymentTxn.setStatus("MOCK_TRANSFER_SUCCESS");

        paymentRepository.save(paymentTxn);
        booking.setPaymentStatus(PaymentStatus.TRANSFER_TO_PROVIDER);
        bookingRepo.save(booking);
    }

    @Transactional
    public void mockPaymentSuccess(Long bookingId){

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        if(booking.getPaymentStatus() != PaymentStatus.UNPAID){
            throw new RuntimeException(
                    "Booking payment already processed");
        }
//        PaymentTransection transaction = paymentRepository.findByBooking(booking)
//                        .orElseThrow(() -> new RuntimeException("Transaction not found"));
        PaymentTransection transaction = paymentRepository.findFirstByBookingAndPaymentStatusIn(
                        booking, List.of(PaymentStatus.UNPAID))
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        transaction.setStatus("SUCCESS");
        transaction.setPaymentStatus(
                PaymentStatus.PAID_TO_PLATFORM);

        booking.setPaymentStatus(
                PaymentStatus.PAID_TO_PLATFORM);
        paymentRepository.save(transaction);
        bookingRepo.save(booking);
    }


    public boolean verifyAndCompletePayment(PaymentVerificationDto verificationDto) throws Exception {

//        PaymentTransection transaction = paymentRepository.findByBookingId(verificationDto.getBookingId())
//                .orElseThrow(() -> new RuntimeException("Transaction not found for this booking"));

        PaymentTransection transaction = paymentRepository.findFirstByBookingIdAndPaymentStatusIn(
                        verificationDto.getBookingId(), List.of(PaymentStatus.UNPAID))
                .orElseThrow(() -> new RuntimeException("Transaction not found for this booking"));

        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", verificationDto.getRazorpayOrderId());
        options.put("razorpay_payment_id", verificationDto.getRazorpayPaymentId());
        options.put("razorpay_signature", verificationDto.getRazorpaySignature());


        boolean isSignatureValid = Utils.verifyPaymentSignature(options, razorPaySecret);

        if (isSignatureValid) {
            transaction.setRazorPayPaymentId(verificationDto.getRazorpayPaymentId());
            transaction.setRazorPaySignature(verificationDto.getRazorpaySignature());
            transaction.setPaymentStatus(PaymentStatus.PAID_TO_PLATFORM); // Update status to Paid
            transaction.setStatus("SUCCESS");
            Payment payment = razorpayClient.payments.fetch(verificationDto.getRazorpayPaymentId());
            String method = payment.get("method");
            transaction.setPaymentMethod(method);
            
            paymentRepository.save(transaction);

            Booking booking = transaction.getBooking();
            booking.setPaymentStatus(PaymentStatus.PAID_TO_PLATFORM);
//            booking.setBookingStatus(BookingStatus.CONFIRMED); // or COMPLETED depending on your logic
            bookingRepo.save(booking);

            return true;
        } else {
            transaction.setPaymentStatus(PaymentStatus.FAILED);
            transaction.setStatus("FAILED");
            paymentRepository.save(transaction);
            throw new RuntimeException("Payment verification failed. Invalid Signature.");
        }
    }


}
