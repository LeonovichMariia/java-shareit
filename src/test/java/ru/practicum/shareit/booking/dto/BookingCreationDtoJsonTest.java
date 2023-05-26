package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingCreationDtoJsonTest {
    @Autowired
    private JacksonTester<BookingCreationDto> json;

    @Test
    void testBookingCreationDto() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .id(1L)
                .status(BookingStatus.WAITING)
                .start(now.plusDays(2))
                .end(now.plusDays(3))
                .itemId(1L)
                .build();
        JsonContent<BookingCreationDto> result = json.write(bookingCreationDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo((now.plusDays(2)).format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo((now.plusDays(3)).format(formatter));
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }
}