package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.messages.LogMessages;
import ru.practicum.shareit.request.dto.AddItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreationRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public AddItemRequest addRequest(@Valid @RequestBody ItemRequestCreationRequest addItemRequest,
                                     @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info(LogMessages.ADD_ITEMREQUEST_REQUEST.toString());
        return itemRequestService.addRequest(addItemRequest, requestorId);
    }

    @GetMapping
    public List<AddItemRequest> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return itemRequestService.getUserRequests(requestorId);
    }

    @GetMapping("all")
    public List<AddItemRequest> getOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                      @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return itemRequestService.getOtherUsersRequests(userId, from, size);
    }

    @GetMapping("{requestId}")
    public AddItemRequest getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}