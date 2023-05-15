package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.messages.LogMessages;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@Valid @RequestBody BookingCreationDto bookingCreationDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(LogMessages.BOOKING_REQUEST.toString(), userId, bookingCreationDto);
        return bookingService.addBooking(bookingCreationDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto renewalBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        log.info(LogMessages.BOOKING_RENEWAL_REQUEST.toString(), bookingId);
        return bookingService.renewalBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {
        log.info(LogMessages.GET_BOOKING_REQUEST.toString(),userId, bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                               @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info(LogMessages.GET_BOOKING_REQUEST_STATUS.toString(), bookerId, state);
        return bookingService.getAllUserBookings(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerAllItemBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                              @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info(LogMessages.GET_ALL_BOOKING_REQUEST_STATUS.toString(), ownerId, state);
        return bookingService.getOwnerAllItemBookings(ownerId, state);
    }
}