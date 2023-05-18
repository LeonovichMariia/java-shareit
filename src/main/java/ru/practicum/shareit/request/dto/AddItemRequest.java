package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.messages.ValidationMessages;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddItemRequest {
    private Long requestId;
    private Long id;
    @NotBlank
    private String name;
    @NotBlank(message = ValidationMessages.EMPTY_DESCRIPTION)
    private String description;
    @NotNull(message = ValidationMessages.AVAILABLE_NULL)
    private Boolean available;
    private LocalDateTime created;
    private List<ItemDto> items;
}