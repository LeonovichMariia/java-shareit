package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.AddItemRequest;

import java.util.List;

public interface ItemRequestService {

    AddItemRequest addRequest(AddItemRequest addItemRequest, Long requestorId);

    List<AddItemRequest> getUserRequests(Long requestorId);

    List<AddItemRequest> getOtherUsersRequests(Long userId, Integer from, Integer size);

    AddItemRequest getItemRequestById(Long userId, Long requestId);
}
