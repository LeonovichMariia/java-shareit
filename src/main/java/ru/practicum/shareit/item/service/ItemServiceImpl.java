package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.IllegalAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.messages.LogMessages;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setOwner(userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                LogMessages.NOT_FOUND.toString() + userId)));
        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto renewalItem(Long itemId, ItemDto itemDto, Long userId) {
        Item selectedItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                LogMessages.NOT_FOUND.toString() + itemId));
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                LogMessages.NOT_FOUND.toString() + userId));
        if (!selectedItem.getOwner().getId().equals(userId)) {
            log.warn(LogMessages.ILLEGAL_ACCESS.toString());
            throw new IllegalAccessException(LogMessages.ILLEGAL_ACCESS.toString());
        }
        Item updatedItem = itemRepository.save(selectedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                LogMessages.NOT_FOUND.toString() + itemId));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getPersonal(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                LogMessages.NOT_FOUND.toString() + userId));
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            log.warn(LogMessages.BLANK_TEXT.toString());
            return Collections.emptyList();
        }
        String lowerText = text.toLowerCase();
        Collection<Item> items = itemRepository.searchItemByText(lowerText);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}