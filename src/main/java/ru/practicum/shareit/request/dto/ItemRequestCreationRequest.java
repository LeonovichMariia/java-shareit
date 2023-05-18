package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.messages.ValidationMessages;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestCreationRequest {
    private Long requestId;
    private String name;
    @NotBlank(message = ValidationMessages.EMPTY_DESCRIPTION)
    private String description;
    private Boolean available;
    private LocalDateTime created;
}
