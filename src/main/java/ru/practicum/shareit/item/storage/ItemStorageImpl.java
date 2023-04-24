package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.messages.LogMessages;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {
    private Long id = 1L;
    private final UserStorage userStorage;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, Set<Long>> userPersonalItems = new HashMap<>();

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        User user = UserMapper.toUser(userStorage.getUserById(userId));
        item.setId(generateId());
        item.setOwner(user);
        items.put(item.getId(), item);
        addToPersonalItems(userId, item.getId());
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto renewalItem(Long itemId, ItemDto itemDto, Long userId) {
        checkIfExist(itemId);
        Item item = items.get(itemId);
        String updatedName = itemDto.getName();
        if (updatedName != null) {
            item.setName(updatedName);
        }
        String updatedDescription = itemDto.getDescription();
        if (updatedDescription != null) {
            item.setDescription(updatedDescription);
        }
        Boolean updatedAvailable = itemDto.getAvailable();
        if (updatedAvailable != null) {
            item.setAvailable(updatedAvailable);
        }
        items.put(itemId, item);
        User user = UserMapper.toUser(userStorage.getUserById(userId));
        addToPersonalItems(user.getId(), item.getId());
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        checkIfExist(itemId);
        Item item = items.get(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getPersonal(Long userId) {
        Set<Long> personalItems = userPersonalItems.get(userId);
        return personalItems.stream()
                .map(items::get)
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
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lowerText)
                        || item.getDescription().toLowerCase().contains(lowerText))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private long generateId() {
        return id++;
    }

    private void addToPersonalItems(Long userId, Long itemId) {
        Set<Long> personalItems = userPersonalItems.getOrDefault(userId, new HashSet<>());
        personalItems.add(itemId);
        userPersonalItems.put(userId, personalItems);
    }

    private void checkIfExist(Long itemId) {
        if (!items.containsKey(itemId)) {
            log.warn(LogMessages.NOT_FOUND.toString(), itemId);
            throw new NotFoundException(LogMessages.NOT_FOUND.toString());
        }
    }
}