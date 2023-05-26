package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BookingException;
import ru.practicum.shareit.exceptions.InvalidIdException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private User user;
    private User user2;
    private Item item;
    private Booking booking;
    private BookingCreationDto bookingCreationDto;

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
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
        bookingCreationDto = BookingCreationDto.builder()
                .id(1L)
                .itemId(item.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    @Test
    public void addBookingAndReturnSavedBooking() {
        when(userRepository.validateUser(anyLong())).thenReturn(user2);
        when(itemRepository.validateItem(anyLong())).thenReturn(item);
        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDto actualBooking = bookingService.addBooking(bookingCreationDto, user2.getId());
        BookingDto expectedBooking = BookingMapper.toBookingDto(booking);

        assertEquals(expectedBooking, actualBooking);
        assertEquals(expectedBooking.getStart(), booking.getStart());
        assertEquals(expectedBooking.getEnd(), booking.getEnd());
        verify(userRepository, times(1)).validateUser(user2.getId());
        verify(itemRepository, times(1)).validateItem(item.getId());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    public void addBookingWhenNotAvailable() {
        item.setAvailable(false);
        when(userRepository.validateUser(anyLong())).thenReturn(user2);
        when(itemRepository.validateItem(anyLong())).thenReturn(item);
        assertThrows(ValidationException.class, () -> bookingService.addBooking(bookingCreationDto, user2.getId()));
        verify(bookingRepository, never()).save(any());
        verify(userRepository, times(1)).validateUser(user2.getId());
        verify(itemRepository, times(1)).validateItem(item.getId());
    }

    @Test
    public void addBookingByOwner() {
        when(userRepository.validateUser(anyLong())).thenReturn(user);
        when(itemRepository.validateItem(anyLong())).thenReturn(item);
        assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingCreationDto, user.getId()));
        verify(bookingRepository, never()).save(any());
        verify(userRepository, times(1)).validateUser(user.getId());
        verify(itemRepository, times(1)).validateItem(item.getId());
    }

    @Test
    public void renewalBookingApproved() {
        long bookingId = booking.getId();
        when(bookingRepository.validateBooking(anyLong())).thenReturn(booking);
        when(userRepository.validateUser(anyLong())).thenReturn(user);
        Booking exitedBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user2)
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingRepository.save(any())).thenReturn(exitedBooking);
        BookingDto actualBooking = bookingService.renewalBooking(bookingId, user.getId(), true);
        assertEquals(BookingMapper.toBookingDto(exitedBooking), actualBooking);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    public void renewalBookingRejected() {
        long bookingId = booking.getId();
        when(bookingRepository.validateBooking(anyLong())).thenReturn(booking);
        when(userRepository.validateUser(anyLong())).thenReturn(user);
        Booking exitedBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user2)
                .status(BookingStatus.REJECTED)
                .build();

        when(bookingRepository.save(any())).thenReturn(exitedBooking);
        BookingDto actualBooking = bookingService.renewalBooking(bookingId, user.getId(), false);
        assertEquals(BookingMapper.toBookingDto(exitedBooking), actualBooking);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    public void renewalBookingWithNoRights() {
        long bookingId = booking.getId();
        when(bookingRepository.validateBooking(anyLong())).thenReturn(booking);
        assertThrows(InvalidIdException.class, () -> bookingService.renewalBooking(bookingId, 99L, true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void renewalBookingWhenAlreadyApproved() {
        long bookingId = booking.getId();
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.validateBooking(anyLong())).thenReturn(booking);
        when(userRepository.validateUser(anyLong())).thenReturn(user);
        assertThrows(BookingException.class, () -> bookingService.renewalBooking(bookingId, user.getId(), true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    public void getBookingById() {
        long userId = user.getId();
        long bookingId = booking.getId();
        BookingDto exitedBookingDto = BookingMapper.toBookingDto(booking);

        when(userRepository.validateUser(anyLong())).thenReturn(user);
        when(bookingRepository.validateBooking(anyLong())).thenReturn(booking);
        BookingDto actualBooking = bookingService.getBookingById(bookingId, userId);
        assertEquals(exitedBookingDto, actualBooking);
        verify(userRepository, times(1)).validateUser(userId);
        verify(bookingRepository, times(1)).validateBooking(bookingId);
    }

    @Test
    public void getAllUserBookings() {
        int from = 0;
        int size = 5;
        User booker = user2;
        List<Booking> expectedList = List.of(booking);
        List<BookingDto> bookingDtoList = List.of(BookingMapper.toBookingDto(booking));
        when(userRepository.validateUser(anyLong())).thenReturn(user2);
        when(bookingRepository.findAllByBookerId(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(expectedList));
        List<BookingDto> actualList = bookingService.getAllUserBookings(booker.getId(), BookingState.ALL, from, size);
        assertEquals(bookingDtoList, actualList);

        when(bookingRepository.findByBookerIdAndStatusIs(anyLong(), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(expectedList));
        actualList = bookingService.getAllUserBookings(booker.getId(), BookingState.WAITING, from, size);
        assertEquals(bookingDtoList, actualList);

        when(bookingRepository.findByBookerIdAndStatusIs(anyLong(), any(BookingStatus.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(expectedList));
        actualList = bookingService.getAllUserBookings(booker.getId(), BookingState.REJECTED, from, size);
        assertEquals(bookingDtoList, actualList);
    }

    @Test
    void getAllByBookerWithWrongStatus() {
        int from = 0;
        int size = 5;
        String bookingStatus = "UNSUPPORTED_STATUS";
        when(userRepository.validateUser(anyLong())).thenReturn(user2);
        BookingException ex = assertThrows(BookingException.class, () -> bookingService.getAllUserBookings(user2.getId(),
                BookingState.valueOf("UNSUPPORTED_STATUS"), from, size));
        assertEquals("Unknown state: " + bookingStatus, ex.getMessage());
    }

    @Test
    public void getOwnerAllItemBookings() {
        int from = 0;
        int size = 5;
        User owner = user;
        List<Booking> expectedList = List.of(booking);
        List<BookingDto> bookingDtoList = List.of(BookingMapper.toBookingDto(booking));
        when(userRepository.validateUser(anyLong())).thenReturn(user);
        when(bookingRepository.findAllByItemOwnerId(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(expectedList));
        List<BookingDto> actualList = bookingService.getOwnerAllItemBookings(owner.getId(), BookingState.ALL, from, size);
        assertEquals(bookingDtoList, actualList);

        when(bookingRepository.findAllByItemOwnerIdAndStatusIs(anyLong(), any(BookingStatus.class),
                any(PageRequest.class))).thenReturn(new PageImpl<>(expectedList));
        actualList = bookingService.getOwnerAllItemBookings(owner.getId(), BookingState.WAITING, from, size);
        assertEquals(bookingDtoList, actualList);

        when(bookingRepository.findAllByItemOwnerIdAndStatusIs(anyLong(), any(BookingStatus.class),
                any(PageRequest.class))).thenReturn(new PageImpl<>(expectedList));
        actualList = bookingService.getOwnerAllItemBookings(owner.getId(), BookingState.REJECTED, from, size);
        assertEquals(bookingDtoList, actualList);
    }

    @Test
    void getOwnerAllItemBookingsWithWrongStatus() {
        int from = 0;
        int size = 5;
        String bookingStatus = "UNSUPPORTED_STATUS";
        when(userRepository.validateUser(anyLong())).thenReturn(user);
        BookingException ex = assertThrows(BookingException.class, () -> bookingService.getOwnerAllItemBookings(user.getId(),
                BookingState.valueOf("UNSUPPORTED_STATUS"), from, size));
        assertEquals("Unknown state: " + bookingStatus, ex.getMessage());
    }
}