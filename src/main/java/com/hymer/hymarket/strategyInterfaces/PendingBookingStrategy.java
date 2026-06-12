package com.hymer.hymarket.strategyInterfaces;

import com.hymer.hymarket.model.Booking;
import com.hymer.hymarket.model.BookingStatus;
import com.hymer.hymarket.service.MailService;
import com.hymer.hymarket.service.MailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PendingBookingStrategy implements BookingNotificationStrategy{
    private final MailService mailService;
    private final MailTemplateService mailTemplateService;
    @Autowired
    public PendingBookingStrategy(MailService mailService, MailTemplateService mailTemplateService) {
        this.mailService = mailService;
        this.mailTemplateService = mailTemplateService;
    }

    @Override
    public BookingStatus getBookingStatus() {
        return BookingStatus.PENDING;
    }

    @Override
    public void sendmail(Booking booking) {
        String serviceName = booking.getServiceOffering().getServiceType().getName();
        String customerName = booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName();
        String providerName = booking.getProvider().getUser().getFirstName();

        String providerSubject = "New Booking Request – NearEase";
        String providerHtml = mailTemplateService.buildNotificationHtml(
                "New Job Request!",
                "Hello " + providerName + ", you have a new booking request for <strong>" + serviceName + "</strong> from <strong>" + customerName + "</strong>. Please open the app to accept or reject this job."
        );
        mailService.sendMail(booking.getProvider().getUser().getEmail(), providerSubject, providerHtml);

        String customerSubject = "Booking Request Sent – NearEase";
        String customerHtml = mailTemplateService.buildNotificationHtml(
                "Request Sent",
                "Your request for <strong>" + serviceName + "</strong> has been sent to <strong>" + providerName + "</strong>. We will notify you as soon as they respond."
        );
        mailService.sendMail(booking.getCustomer().getEmail(), customerSubject, customerHtml);


    }
}
