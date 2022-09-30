package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> createItem(@Validated({Create.class}) @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received request to create item from the userId={}, itemDto={}", userId, itemDto);
        return itemClient.createItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable Long itemId,
                                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Validated({Create.class}) @RequestBody CommentRequestDto commentDto) {
        log.info("Received request to create comment from the userId={}, commentDto={}", userId, commentDto);
        return itemClient.createComment(itemId, userId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        log.info("Received request to update item from the itemId={}, userId={}, itemDto={}", itemId, userId, itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Received request to get item from the userId={}, itemId={}", userId, itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                              @Positive @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("Received request to get all items userId={}, from={}, size={}", userId, from, size);
        return itemClient.getAllItems(from, size, userId);
    }

    @DeleteMapping(value = "/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long itemId) {
        return  itemClient.deleteItem(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByNameAndDesctription(@RequestParam String text,
                                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received request to search items by name and desctription userId={}, text={}", userId, text);
        return itemClient.searchItemByNameAndDesctription(text, userId);
    }
}