package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.messages.ValidationMessages;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
public class BookingDto {
    private long id;
    private LocalDateTime start;
    @FutureOrPresent(message = ValidationMessages.END_DATA)
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;
}