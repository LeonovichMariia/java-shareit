package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.AlreadyExistException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.messages.LogMessages;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserStorageImpl implements UserStorage {
    private Long id = 1L;
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    protected void checkDuplication(UserDto userDto) {
        if (emails.contains(userDto.getEmail())) {
            log.warn(LogMessages.ALREADY_EXIST.toString(), userDto);
            throw new AlreadyExistException(LogMessages.ALREADY_EXIST.toString());
        }
    }

    public void checkIfExist(Long userId) {
        if (!users.containsKey(userId)) {
            log.warn(LogMessages.NOT_FOUND.toString(), userId);
            throw new NotFoundException(LogMessages.NOT_FOUND.toString());
        }
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        checkDuplication(userDto);
        User user = UserMapper.toUser(userDto);
        user.setId(generateId());
        users.put(user.getId(), user);
        emails.add(userDto.getEmail());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto renewalUser(UserDto userDto, Long userId) {
        checkIfExist(userId);
        User user = users.get(userId);
        String updatedName = userDto.getName();
        if (updatedName != null) {
            user.setName(updatedName);
        }
        String updatedEmail = userDto.getEmail();
        if (updatedEmail != null && !updatedEmail.equals(user.getEmail())) {
            checkDuplication(userDto);
            emails.remove(user.getEmail());
            user.setEmail(updatedEmail);
            emails.add(userDto.getEmail());
        }
        users.put(userId, user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUserById(Long userId) {
        checkIfExist(userId);
        User user = users.get(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void removeUserById(Long userId) {
        checkIfExist(userId);
        User user = UserMapper.toUser(getUserById(userId));
        emails.remove(user.getEmail());
        users.remove(userId);
    }

    private long generateId() {
        return id++;
    }
}