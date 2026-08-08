package com.hymer.hymarket;

import com.hymer.hymarket.Repository.BookingRepo;
import com.hymer.hymarket.Repository.ServiceOfferingRepo;
import com.hymer.hymarket.Repository.UserRepository;
import com.hymer.hymarket.dto.BookingRequestDto;
import com.hymer.hymarket.dto.BookingResponseDto;
import com.hymer.hymarket.model.Booking;
import com.hymer.hymarket.model.ProviderProfile;
import com.hymer.hymarket.model.ServiceOffering;
import com.hymer.hymarket.model.User;
import com.hymer.hymarket.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    private BookingService bookingService;
    @Mock
    private BookingRepo bookingRepo;
    @Mock
    private ServiceOfferingRepo serviceOfferingRepo;
    @Mock
    private UserRepository userRepository;
    @Test
    public void createBookingTest(){
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setScheduleTime(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setWorkLocation("Work Location");
        bookingRequestDto.setCustomerRequest("Customer Request");
        bookingRequestDto.setServiceOfferingId(1L);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "customer@gmail.com",
                        null
                )
        );
        SecurityContextHolder.setContext(securityContext);
        User customer = new User();
        customer.setId(1L);
        customer.setEmail("customer@gmail.com");
        User providerUser = new User();
        providerUser.setId(2L);
        ProviderProfile providerProfile = new ProviderProfile();
        providerProfile.setUser(providerUser);
        ServiceOffering offering = new ServiceOffering();
        offering.setId(1L);
        offering.setPrice(500.00);
        offering.setProviderProfile(providerProfile);
        when(userRepository.findByEmail("customer@gmail.com"))
                .thenReturn(Optional.of(customer));
        when(serviceOfferingRepo.findById(1))
                .thenReturn(Optional.of(offering));
        when(bookingRepo.save(any(Booking.class)))
                .thenAnswer(invocation -> {
                    Booking booking = invocation.getArgument(0);
                    booking.setId(100L);
                    return booking;
                });
        BookingResponseDto response =
                bookingService.createBooking(bookingRequestDto);
        assertNotNull(response);
        verify(bookingRepo).save(any(Booking.class));
    }


}
