package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.AddItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public AddItemRequest addRequest(AddItemRequest addItemRequest, Long requestorId) {
        User user = userRepository.validateUser(requestorId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(addItemRequest, user);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toAddItemRequest(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<AddItemRequest> getUserRequests(Long requestorId) {
        User user = userRepository.validateUser(requestorId);
        return itemRequestRepository.findAllByRequestorId(user.getId()).stream()
                .map(ItemRequestMapper::toAddItemRequest)
                .collect(Collectors.toList());
    }

    @Override
    public List<AddItemRequest> getOtherUsersRequests(Long userId, Integer from, Integer size) {
        userRepository.validateUser(userId);
        PageRequest pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRequestRepository.findAllByRequestorIdNot(userId, pageable)
                .map(ItemRequestMapper::toAddItemRequest)
                .getContent();
    }

    @Override
    public AddItemRequest getItemRequestById(Long userId, Long requestId) {
        userRepository.validateUser(userId);
        ItemRequest itemRequest = itemRequestRepository.validateItemRequest(requestId);
        return ItemRequestMapper.toAddItemRequest(itemRequest);
    }
}
