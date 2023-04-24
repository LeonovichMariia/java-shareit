package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.messages.LogMessages;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    private void validate(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            log.warn(LogMessages.EMPTY_USER_NAME.toString());
            throw new ValidationException(LogMessages.EMPTY_NAME.toString());
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            log.warn(LogMessages.EMPTY_EMAIL.toString());
            throw new ValidationException(LogMessages.EMPTY_EMAIL.toString());
        }
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        validate(userDto);
        return userStorage.addUser(userDto);
    }

    @Override
    public UserDto renewalUser(UserDto userDto, Long userId) {
        return userStorage.renewalUser(userDto, userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return userStorage.getUserById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public void removeUserById(Long userId) {
        userStorage.removeUserById(userId);
    }
}