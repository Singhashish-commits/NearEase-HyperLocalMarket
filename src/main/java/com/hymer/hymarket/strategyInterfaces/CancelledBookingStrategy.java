package com.hymer.hymarket.strategyInterfaces;

import com.hymer.hymarket.model.Booking;
import com.hymer.hymarket.model.BookingStatus;
import com.hymer.hymarket.service.MailService;
import com.hymer.hymarket.service.MailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CancelledBookingStrategy  implements BookingNotificationStrategy{
    private final MailService mailService;
    private final MailTemplateService mailTemplateService;
    @Autowired
    public CancelledBookingStrategy(MailService mailService, MailTemplateService mailTemplateService) {
        this.mailService = mailService;
        this.mailTemplateService = mailTemplateService;
    }

    @Override
    public BookingStatus getBookingStatus() {
        return BookingStatus.CANCELLED;
    }

    @Override
    public void sendmail(Booking booking) {

            String serviceName = booking.getServiceOffering().getServiceType().getName();
            String providerName = booking.getProvider().getUser().getFirstName() + " " + booking.getProvider().getUser().getLastName();
            String customerName = booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName();

            String customerSubject = "Booking Cancelled – NearEase";
            String customerHtml = mailTemplateService.buildNotificationHtml(
                    "Booking Cancelled",
                    "Your booking for <strong>" + serviceName + "</strong> with <strong>" + providerName + "</strong> has been successfully cancelled."
            );
            mailService.sendMail(booking.getCustomer().getEmail(), customerSubject, customerHtml);

            String providerSubject = "Job Cancelled – NearEase";
            String providerHtml = mailTemplateService.buildNotificationHtml(
                    "Job Cancelled",
                    "The booking for <strong>" + serviceName + "</strong> requested by <strong>" + customerName + "</strong> has been cancelled. No further action is required."
            );
            mailService.sendMail(booking.getProvider().getUser().getEmail(), providerSubject, providerHtml);

    }
}
