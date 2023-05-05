package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.messages.LogMessages;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    @Transactional
    public UserDto renewalUser(UserDto userDto, Long userId) {
        User selectedUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                LogMessages.NOT_FOUND.toString() + userId));
        String updatedName = userDto.getName();
        if (updatedName != null) {
            selectedUser.setName(updatedName);
        }
        String updatedEmail = userDto.getEmail();
        if (updatedEmail != null) {
            selectedUser.setEmail(updatedEmail);
        }
        User updatedUser = userRepository.save(selectedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                LogMessages.NOT_FOUND.toString() + userId));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void removeUserById(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                LogMessages.NOT_FOUND.toString() + userId));
        userRepository.deleteById(userId);
    }
}