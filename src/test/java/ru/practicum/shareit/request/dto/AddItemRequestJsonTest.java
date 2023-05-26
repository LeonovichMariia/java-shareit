package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class AddItemRequestJsonTest {
    @Autowired
    private JacksonTester<AddItemRequest> json;

    @Test
    void testItemRequestDto() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        LocalDateTime now = LocalDateTime.now();
        AddItemRequest addItemRequest = AddItemRequest.builder()
                .id(1L)
                .requestor(1L)
                .description("item description")
                .created(now)
                .build();
        JsonContent<AddItemRequest> result = json.write(addItemRequest);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requestor").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(addItemRequest.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(now.format(formatter));
        assertThat(result).hasEmptyJsonPathValue("$.items");
    }
}