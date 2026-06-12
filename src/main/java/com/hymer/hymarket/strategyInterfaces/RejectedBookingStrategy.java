package com.hymer.hymarket.strategyInterfaces;

import com.hymer.hymarket.model.Booking;
import com.hymer.hymarket.model.BookingStatus;
import com.hymer.hymarket.service.MailService;
import com.hymer.hymarket.service.MailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RejectedBookingStrategy implements BookingNotificationStrategy{
    private final MailService mailService;
    private final MailTemplateService mailTemplateService;
    @Autowired
    public RejectedBookingStrategy(MailService mailService, MailTemplateService mailTemplateService) {
        this.mailService = mailService;
        this.mailTemplateService = mailTemplateService;
    }

    @Override
    public BookingStatus getBookingStatus() {
        return BookingStatus.REJECTED;
    }

    @Override
    public void sendmail(Booking booking) {
        String serviceName = booking.getServiceOffering().getServiceType().getName();
        String providerName = booking.getProvider().getUser().getFirstName() + " " + booking.getProvider().getUser().getLastName();

        String subject = "Booking Rejected – NearEase";
        String htmlContent = mailTemplateService.buildNotificationHtml(
                "Booking Rejected",
                "Unfortunately, your booking for <strong>" + serviceName + "</strong> has been rejected by <strong>" + providerName + "</strong>. Please explore other providers on the app to get your service done."
        );

        mailService.sendMail(booking.getCustomer().getEmail(), subject, htmlContent);



    }
}
