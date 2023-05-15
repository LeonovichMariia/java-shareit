package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.IllegalAccessException;
import ru.practicum.shareit.exceptions.RequestException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.messages.LogMessages;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    public static final Sort SORT_BY_START_ASC = Sort.by("start").ascending();
    public static final Sort SORT_BY_START_DESC = Sort.by("start").descending();

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item newItem = ItemMapper.toItem(itemDto);
        User user = userRepository.validateUser(userId);
        newItem.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto renewalItem(Long itemId, ItemDto itemDto, Long userId) {
        Item item = itemRepository.validateItem(itemId);
        userRepository.validateUser(userId);
        if (!item.getOwner().getId().equals(userId)) {
            log.warn(LogMessages.ILLEGAL_ACCESS.toString());
            throw new IllegalAccessException(LogMessages.ILLEGAL_ACCESS.toString());
        }
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
        ItemMapper.toItemDto(item);
        Item updatedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    @Transactional
    public ItemDto getItemById(Long itemId, Long ownerId) {
        Item item = itemRepository.validateItem(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        setComments(itemDto, comments);
        if (item.getOwner().getId().equals(ownerId)) {
            setBookings(itemDto);
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getPersonal(Long userId) {
        userRepository.validateUser(userId);
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .peek(itemDto -> {
                    Long itemId = itemDto.getId();
                    List<Comment> comments = commentRepository.findAllByItemId(itemId);
                    setComments(itemDto, comments);
                    setBookings(itemDto);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            log.warn(LogMessages.BLANK_TEXT.toString());
            return Collections.emptyList();
        }
        String lowerText = text.toLowerCase();
        Collection<Item> items = itemRepository.searchItemByText(lowerText);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.validateUser(userId);
        Item item = itemRepository.validateItem(itemId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> items = bookingRepository
                .findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(itemId, userId, BookingStatus.APPROVED, now);
        if (items.isEmpty()) {
            log.warn(LogMessages.REQUEST_EXCEPTION.toString(), userId, itemId);
            throw new RequestException(LogMessages.REQUEST_EXCEPTION.toString() + userId + itemId);
        }
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, user, item));
        return CommentMapper.toCommentDto(comment);
    }

    private void setComments(ItemDto itemDto, List<Comment> comments) {
        itemDto.setComments(comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
    }

    public void setBookings(ItemDto itemDto) {
        Long itemId = itemDto.getId();
        Booking lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatus(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED, SORT_BY_START_DESC).orElse(null);
        itemDto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking));
        Booking nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatus(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED, SORT_BY_START_ASC).orElse(null);
        itemDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking));
    }
}