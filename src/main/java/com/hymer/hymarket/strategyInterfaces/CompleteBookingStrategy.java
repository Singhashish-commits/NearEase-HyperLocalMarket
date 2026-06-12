package com.hymer.hymarket.strategyInterfaces;

import com.hymer.hymarket.model.Booking;
import com.hymer.hymarket.model.BookingStatus;
import com.hymer.hymarket.service.MailService;
import com.hymer.hymarket.service.MailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompleteBookingStrategy implements BookingNotificationStrategy {
    private final MailService mailService;
    private final MailTemplateService mailTemplateService;
    @Autowired
    public CompleteBookingStrategy(MailService mailService, MailTemplateService mailTemplateService) {
        this.mailService = mailService;
        this.mailTemplateService = mailTemplateService;
    }

    @Override
    public BookingStatus getBookingStatus() {
        return BookingStatus.COMPLETED;
    }

    @Override
    public void sendmail(Booking booking) {

        String serviceName = booking.getServiceOffering().getServiceType().getName();
        String providerName = booking.getProvider().getUser().getFirstName() + " " + booking.getProvider().getUser().getLastName();
        String customerName = booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName();

        // 1. Notify Customer
        String customerSubject = "Service Completed – NearEase";
        String customerHtml = mailTemplateService.buildNotificationHtml(
                "Booking Completed",
                "Your <strong>" + serviceName + "</strong> service by <strong>" + providerName + "</strong> has been successfully completed. Check their portfolio for before & after photos!"
        );
        mailService.sendMail(booking.getCustomer().getEmail(), customerSubject, customerHtml);

        // 2. Notify Provider
        String providerSubject = "Job Completed – NearEase";
        String providerHtml = mailTemplateService.buildNotificationHtml(
                "Great Job!",
                "You have successfully completed the <strong>" + serviceName + "</strong> service for <strong>" + customerName + "</strong>. The before & after photos have been added to your portfolio."
        );
        mailService.sendMail(booking.getProvider().getUser().getEmail(), providerSubject, providerHtml);


    }
}
