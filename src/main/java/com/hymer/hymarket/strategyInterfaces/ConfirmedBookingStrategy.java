package com.hymer.hymarket.strategyInterfaces;

import com.hymer.hymarket.model.Booking;
import com.hymer.hymarket.model.BookingStatus;
import com.hymer.hymarket.service.MailService;
import com.hymer.hymarket.service.MailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfirmedBookingStrategy implements BookingNotificationStrategy {
    private final MailService mailService;
    private final MailTemplateService mailTemplateService;
    @Autowired
    public ConfirmedBookingStrategy(MailService mailService, MailTemplateService mailTemplateService) {
        this.mailService = mailService;
        this.mailTemplateService = mailTemplateService;
    }


    @Override
    public BookingStatus getBookingStatus() {
        return BookingStatus.CONFIRMED;
    }

    @Override
    public void sendmail(Booking booking) {
        String serviceName = booking.getServiceOffering().getServiceType().getName();
        String providerName = booking.getProvider().getUser().getFirstName() + " " + booking.getProvider().getUser().getLastName();

        String subject = "Booking Accepted – NearEase";
        String htmlContent = mailTemplateService.buildNotificationHtml(
                "Booking Accepted",
                "Your booking for <strong>" + serviceName + "</strong> has been accepted by <strong>" + providerName + "</strong>. They will reach you shortly."
        );

        mailService.sendMail(booking.getCustomer().getEmail(), subject, htmlContent);

    }
}
