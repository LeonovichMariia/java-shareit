package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto renewalItem(Long itemId, ItemDto itemDto, Long userId);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getPersonal(Long userId);

    List<ItemDto> search(String text);
}