package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.util.Collection;

public interface BookingService {
    BookingDtoCreate createBooking(Long userId, BookingDto bookingDto);

    BookingDtoCreate bookingApproval(Long userId, Long bookingId, Boolean approved);

    BookingDtoCreate findBookingById(Long userId, Long bookingId);

    Collection<BookingDtoCreate> findBookingByUser(Long userId, String state);

    Collection<BookingDtoCreate> findBookingByOwner(Long userId, String state);
}
