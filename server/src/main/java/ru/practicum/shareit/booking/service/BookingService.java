package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingCreationDto bookingCreationDto, Long userId);

    BookingDto renewalBooking(Long bookingId, Long userId, Boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getAllUserBookings(Long bookerId, BookingState state, Integer from, Integer size);

    List<BookingDto> getOwnerAllItemBookings(Long userId, BookingState state, Integer from, Integer size);
}