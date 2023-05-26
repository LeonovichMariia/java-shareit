package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AddItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;
    private User user;
    private User user2;
    private AddItemRequest addItemRequest;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Name")
                .email("username@gmail.com")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("Name2")
                .email("user2name@gmail.com")
                .build();
        item = Item.builder()
                .id(1L)
                .name("Item Name")
                .description("Item description")
                .owner(user)
                .available(true)
                .request(itemRequest)
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
        addItemRequest = AddItemRequest.builder()
                .id(itemRequest.getId())
                .requestor(itemRequest.getRequestor().getId())
                .description("Description")
                .created(itemRequest.getCreated())
                .items(List.of())
                .build();
    }

    @Test
    public void addItemRequestAndReturnSavedRequest() {
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        AddItemRequest actualNewRequest = itemRequestService.addRequest(addItemRequest, user.getId());
        assertNotNull(actualNewRequest);
        assertEquals(addItemRequest, actualNewRequest);
        assertEquals(addItemRequest.getId(), actualNewRequest.getId());
        assertEquals(addItemRequest.getDescription(), actualNewRequest.getDescription());
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    public void getUserRequests() {
        Long userId = user.getId();
        List<ItemRequest> requestList = new ArrayList<>();
        requestList.add(itemRequest);
        List<AddItemRequest> addItemList = List.of(
                ItemRequestMapper.toAddItemRequest(requestList.get(0)));

        when(userRepository.validateUser(anyLong())).thenReturn(user);
        when(itemRequestRepository.findAllByRequestorId(anyLong()))
                .thenReturn(requestList);
        List<AddItemRequest> actualRequestList = itemRequestService.getUserRequests(userId);
        assertEquals(addItemList, actualRequestList);
        verify(itemRequestRepository, times(1)).findAllByRequestorId(userId);
    }

    @Test
    public void getOtherUsersRequests() {
        int from = 0;
        int size = 5;
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        Long userId = user.getId();
        List<AddItemRequest> addItemList = Collections.emptyList();

        when(userRepository.validateUser(anyLong())).thenReturn(user);
        when(itemRequestRepository.findAllByRequestorIdNot(userId, page)).thenReturn(Page.empty());
        List<AddItemRequest> actualRequestList = itemRequestService.getOtherUsersRequests(userId, from, size);
        assertEquals(addItemList, actualRequestList);
        verify(itemRequestRepository, times(1)).findAllByRequestorIdNot(userId, page);
    }

    @Test
    public void getItemRequestById() {
        AddItemRequest expectedRequest = ItemRequestMapper.toAddItemRequest(itemRequest);

        when(itemRequestRepository.validateItemRequest(anyLong())).thenReturn(itemRequest);
        AddItemRequest actualRequest = itemRequestService.getItemRequestById(user.getId(), itemRequest.getId());
        assertNotNull(actualRequest);
        assertEquals(expectedRequest.getId(), actualRequest.getId());
        assertEquals(expectedRequest.getDescription(), actualRequest.getDescription());
        assertEquals(expectedRequest, actualRequest);
    }

    @Test
    public void getItemRequestByNotFoundId() {
        when(itemRequestRepository.validateItemRequest(itemRequest.getId())).thenThrow(new NotFoundException(""));
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(user.getId(), itemRequest.getId()));
    }
}