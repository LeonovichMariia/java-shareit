package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.IllegalAccessException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private User user;
    private User user2;
    private Item item;
    private ItemDto itemDto;
    private ItemRequest itemRequest;
    private Comment comment;
    private Booking booking;

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
                .name("Item name")
                .description("Item description")
                .available(true)
                .owner(user)
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .owner(user)
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Item request description")
                .requestor(user2)
                .created(LocalDateTime.now())
                .build();
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        comment = Comment.builder()
                .id(1L)
                .author(user2)
                .item(item)
                .text("Comment to item")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    public void addItemAndReturnSavedItemWithoutRequest() {
        ItemDto expectedItem = ItemMapper.toItemDto(item);
        when(userRepository.validateUser(anyLong())).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto actualItemDto = itemService.addItem(user.getId(), itemDto);

        assertEquals(expectedItem, actualItemDto);
        assertEquals(expectedItem.getName(), actualItemDto.getName());
        assertEquals(expectedItem.getDescription(), actualItemDto.getDescription());
        assertEquals(expectedItem.getRequestId(), actualItemDto.getRequestId());
        assertNull(actualItemDto.getRequestId());
        verify(itemRepository, times(1)).save(any());
        verify(itemRequestRepository, never()).findById(1L);
    }

    @Test
    public void addItemAndReturnSavedItemWithRequest() {
        item.setRequest(itemRequest);
        ItemDto expectedItem = ItemMapper.toItemDto(item);
        when(userRepository.validateUser(anyLong())).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto actualItemDto = itemService.addItem(user.getId(), itemDto);

        assertEquals(expectedItem, actualItemDto);
        assertEquals(expectedItem.getName(), actualItemDto.getName());
        assertEquals(expectedItem.getDescription(), actualItemDto.getDescription());
        assertEquals(expectedItem.getRequestId(), actualItemDto.getRequestId());
        verify(itemRepository, times(1)).save(any());
        verify(itemRequestRepository, never()).findById(1L);
    }

    @Test
    public void renewalItem() {
        itemDto.setDescription("New item description");
        itemDto.setName("New item name");
        Item itemNew = ItemMapper.toItem(itemDto);
        ItemDto expectedItem = ItemMapper.toItemDto(itemNew);
        when(itemRepository.validateItem(anyLong())).thenReturn(item);
        when(itemRepository.save(any())).thenReturn(ItemMapper.toItem(itemDto));

        ItemDto actualItemDto = itemService.renewalItem(item.getId(), itemDto, user.getId());

        assertEquals(expectedItem, actualItemDto);
        assertEquals(expectedItem.getName(), actualItemDto.getName());
        assertEquals(expectedItem.getDescription(), actualItemDto.getDescription());
        assertEquals(expectedItem.getRequestId(), actualItemDto.getRequestId());
        verify(itemRepository, times(1)).save(any());
        verify(itemRequestRepository, never()).findById(1L);
    }

    @Test
    public void renewalItemByNotOwner() {
        itemDto.setDescription("New item description");
        itemDto.setName("New item name");
        when(itemRepository.validateItem(anyLong())).thenReturn(item);

        assertThrows(IllegalAccessException.class, () -> itemService.renewalItem(item.getId(), itemDto, user2.getId()));
        verify(itemRepository, times(1)).validateItem(any());
    }

    @Test
    public void getItemByIdWithoutBookingAndComments() {
        ItemDto expectedItemDto = ItemMapper.toItemDto(item);
        expectedItemDto.setComments(Collections.emptyList());
        long expectedItemId = item.getId();
        long expectedUserId = user.getId();

        when(itemRepository.validateItem(anyLong())).thenReturn(item);
        ItemDto actualItemDto = itemService.getItemById(expectedItemId, expectedUserId);

        assertEquals(expectedItemDto, actualItemDto);
        assertEquals(item.getName(), actualItemDto.getName());
        assertEquals(item.getDescription(), actualItemDto.getDescription());
        verify(itemRepository, times(1)).validateItem(expectedItemId);
        verify(commentRepository, times(1)).findAllByItemId(expectedItemId);
    }

    @Test
    public void getItemById() {
        booking.setStatus(BookingStatus.APPROVED);
        long expectedItemId = item.getId();
        long expectedUserId = user.getId();
        BookingDto bookingDto1 = BookingMapper.toBookingDto(booking);
        BookingShortDto bookingShortDto = BookingMapper.toBookingShortDto(bookingDto1);
        ItemDto expectedItemDto = ItemMapper.toItemDto(item);
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        expectedItemDto.setComments(List.of(commentDto));
        expectedItemDto.setLastBooking(bookingShortDto);
        when(itemRepository.validateItem(anyLong())).thenReturn(item);
        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatus(anyLong(), any(LocalDateTime.class),
                any(BookingStatus.class), any(Sort.class))).thenReturn(Optional.of(booking));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));
        ItemDto actualItem = itemService.getItemById(expectedItemId, expectedUserId);
        assertEquals(expectedItemDto, actualItem);

        verify(itemRepository, times(1)).validateItem(expectedItemId);
        verify(bookingRepository, times(1)).findFirstByItemIdAndStartBeforeAndStatus(anyLong(),
                any(LocalDateTime.class), any(BookingStatus.class), any(Sort.class));
        verify(commentRepository, times(1)).findAllByItemId(expectedItemId);
    }

    @Test
    public void getPersonal() {
        ItemDto expectedItemDto = ItemMapper.toItemDto(item);
        int from = 0;
        int size = 5;
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        Booking nextBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .item(item)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        BookingDto bookingDto1 = BookingMapper.toBookingDto(booking);
        BookingShortDto bookingShortDto = BookingMapper.toBookingShortDto(bookingDto1);
        BookingDto bookingDto2 = BookingMapper.toBookingDto(nextBooking);
        BookingShortDto bookingShortDto2 = BookingMapper.toBookingShortDto(bookingDto2);
        expectedItemDto.setLastBooking(bookingShortDto);
        expectedItemDto.setNextBooking(bookingShortDto2);
        when(userRepository.validateUser(anyLong())).thenReturn(user);
        when(itemRepository.findAllByOwnerId(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(item)));
        when(bookingRepository.findAllByOwnerAndStatus(anyLong(), any(BookingStatus.class), any(Sort.class))).thenReturn(List.of(booking));
        List<ItemDto> actualDto = itemService.getPersonal(user.getId(), from, size);
        assertNotNull(actualDto);
        assertEquals(1, actualDto.size());
        assertEquals(expectedItemDto.getId(), actualDto.get(0).getId());
        assertEquals(booking.getId(), actualDto.get(0).getNextBooking().getId());
        assertNotNull(actualDto.get(0).getComments());

        verify(itemRepository, times(1)).findAllByOwnerId(user.getId(), page);
        verify(bookingRepository, times(1)).findAllByOwnerAndStatus(anyLong(), any(BookingStatus.class), any(Sort.class));
    }

    @Test
    public void search() {
        int from = 0;
        int size = 5;
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        String text = "description";
        when(itemRepository.searchItemByText(anyString(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(item)));
        List<ItemDto> foundedItems = itemService.search(text, from, size);
        assertFalse(foundedItems.isEmpty());
        assertEquals(1, foundedItems.size());
        assertEquals(ItemMapper.toItemDto(item), foundedItems.get(0));

        verify(itemRepository, times(1)).searchItemByText(text, page);
    }

    @Test
    public void searchNotFoundItem() {
        String text = "jfghjhk";
        int from = 0;
        int size = 5;
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);
        when(itemRepository.searchItemByText(anyString(), any(PageRequest.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        List<ItemDto> foundedItems = itemService.search(text, from, size);

        assertTrue(foundedItems.isEmpty());
        verify(itemRepository, times(1)).searchItemByText(text, page);
    }

    @Test
    public void addComment() {
        long expectedUserId = user2.getId();
        long expectedItemId = item.getId();
        CommentDto commentDto = CommentDto.builder()
                .text("Comment for item1")
                .build();
        Comment expectedComment = Comment.builder()
                .item(item)
                .text("Comment for item1")
                .author(user2)
                .created(LocalDateTime.now())
                .build();

        when(userRepository.validateUser(anyLong())).thenReturn(user);
        when(bookingRepository.findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any())).thenReturn(expectedComment);
        CommentDto actualComment = itemService.addComment(expectedUserId, expectedItemId, commentDto);
        assertEquals(CommentMapper.toCommentDto(expectedComment), actualComment);
    }
}