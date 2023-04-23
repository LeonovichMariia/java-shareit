package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.messages.ValidationMessages;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(message = ValidationMessages.EMPTY_NAME)
    private String name;
    @NotBlank(message = ValidationMessages.EMPTY_DESCRIPTION)
    private String description;
    @NotNull
    private Boolean available;
    private Long owner;
    private ItemRequest request;
}