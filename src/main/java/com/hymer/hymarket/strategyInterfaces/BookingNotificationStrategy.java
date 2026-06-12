package com.hymer.hymarket.strategyInterfaces;

import com.hymer.hymarket.model.Booking;
import com.hymer.hymarket.model.BookingStatus;

public interface BookingNotificationStrategy {

    BookingStatus getBookingStatus();
    void sendmail(Booking booking);
}
