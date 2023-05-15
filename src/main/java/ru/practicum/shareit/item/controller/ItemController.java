package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.messages.LogMessages;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        log.info(LogMessages.ADD_REQUEST.toString(), itemDto);
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto renewalItem(@PathVariable Long itemId,
                               @RequestBody ItemDto itemDto,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(LogMessages.RENEWAL_REQUEST.toString(), itemId, itemDto);
        return itemService.renewalItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                               @PathVariable Long itemId) {
        log.info(LogMessages.GET_BY_ID_REQUEST.toString(), itemId);
        return itemService.getItemById(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> getPersonalItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info(LogMessages.GET_ALL_REQUEST.toString());
        return itemService.getPersonal(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        log.info(LogMessages.SEARCH_REQUEST.toString());
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.info(LogMessages.COMMENT_REQUEST.toString(), itemId, userId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}