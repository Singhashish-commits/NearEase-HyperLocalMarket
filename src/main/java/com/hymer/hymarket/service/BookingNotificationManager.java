package com.hymer.hymarket.service;

import com.hymer.hymarket.model.Booking;
import com.hymer.hymarket.model.BookingStatus;
import com.hymer.hymarket.strategyInterfaces.BookingNotificationStrategy;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class BookingNotificationManager {
    private final Map<BookingStatus, BookingNotificationStrategy> strategyMap;

    public BookingNotificationManager(List<BookingNotificationStrategy> strategies) {
        strategyMap = new EnumMap<>(BookingStatus.class);
        for (BookingNotificationStrategy strategy : strategies) {
            strategyMap.put(strategy.getBookingStatus(), strategy);
        }
    }

    public void triggerNotification(Booking booking, BookingStatus newStatus) {
        BookingNotificationStrategy strategy = strategyMap.get(newStatus);
        if (strategy != null) {
            strategy.sendmail(booking);
        }

    }
}
