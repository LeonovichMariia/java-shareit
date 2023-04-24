package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    UserDto addUser(UserDto userDto);

    UserDto renewalUser(UserDto userDto, Long userId);

    User getUserById(Long userId);

    List<UserDto> getAllUsers();

    void removeUserById(Long userId);

    void checkIfExist(Long userId);
}