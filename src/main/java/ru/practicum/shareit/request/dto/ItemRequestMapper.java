package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(AddItemRequest addItemRequest, User user) {
        return ItemRequest.builder()
                .id(addItemRequest.getId())
                .requestor(user)
                .description(addItemRequest.getDescription())
                .created(addItemRequest.getCreated())
                .build();
    }

    public static AddItemRequest toAddItemRequest(ItemRequest itemRequest) {
        List<Item> items = itemRequest.getItems();
        return AddItemRequest.builder()
                .id(itemRequest.getId())
                .requestor(itemRequest.getRequestor().getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items((items == null || items.isEmpty()) ? Collections.emptyList() : items.stream()
                                .map(ItemMapper::toItemDto)
                                .collect(Collectors.toList()))
                .build();
    }
}
