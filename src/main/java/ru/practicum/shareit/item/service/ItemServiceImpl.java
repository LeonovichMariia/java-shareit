package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.IllegalAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.messages.LogMessages;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage storage;
    private final UserStorage userStorage;

    private void validate(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            log.warn(LogMessages.AVAILABLE_NULL.toString(), itemDto);
            throw new ValidationException(LogMessages.AVAILABLE_NULL.toString());
        }
        if (itemDto.getDescription() == null) {
            log.warn(LogMessages.EMPTY_DESCRIPTION.toString(), itemDto);
            throw new ValidationException(LogMessages.EMPTY_DESCRIPTION.toString());
        }
        if (itemDto.getName() == null || itemDto.getName().equals("") || itemDto.getName().isEmpty()) {
            log.warn(LogMessages.EMPTY_NAME.toString(), itemDto);
            throw new ValidationException(LogMessages.EMPTY_NAME.toString());
        }
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        userStorage.checkIfExist(userId);
        validate(itemDto);
        return storage.addItem(userId, itemDto);
    }

    @Override
    public ItemDto renewalItem(Long itemId, ItemDto itemDto, Long userId) {
        userStorage.checkIfExist(userId);
        ItemDto item = storage.getItemById(itemId);
        if (item == null) {
            log.warn(LogMessages.NOT_FOUND.toString());
            throw new NotFoundException(LogMessages.NOT_FOUND.toString());
        }
        if (!Objects.equals(item.getOwner(), userId)) {
            log.warn(LogMessages.ILLEGAL_ACCESS.toString());
            throw new IllegalAccessException(LogMessages.ILLEGAL_ACCESS.toString());
        }
        return storage.renewalItem(itemId, itemDto, userId);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return storage.getItemById(itemId);
    }

    @Override
    public List<ItemDto> getPersonal(Long userId) {
        userStorage.checkIfExist(userId);
        return storage.getPersonal(userId);
    }

    @Override
    public List<ItemDto> search(String text) {
        return storage.search(text);
    }
}