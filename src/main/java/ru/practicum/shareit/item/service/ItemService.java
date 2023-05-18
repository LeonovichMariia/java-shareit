package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.AddItemRequest;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, AddItemRequest itemDto);

    ItemDto renewalItem(Long itemId, ItemDto itemDto, Long userId);

    ItemDto getItemById(Long itemId, Long ownerId);

    List<ItemDto> getPersonal(Long userId, Integer from, Integer size);

    List<ItemDto> search(String text, Integer from, Integer size);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}