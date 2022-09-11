package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import java.util.List;

@RequestMapping("/items")
@Controller
public class ItemController {
    private final ItemService service;

    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Validated({Create.class}) @RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(service.createItem(itemDto, userId));
    }
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> createComment(@Validated({Create.class})
                                                            @RequestBody CommentRequestDto commentDto,
                                                            @PathVariable Long itemId,
                                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(service.createComment(commentDto, itemId, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId,
                                              @Validated({Update.class}) @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(service.updateItem(userId, itemId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @PathVariable Long itemId) {
        return ResponseEntity.ok().body(service.getItem(userId, itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(service.getAllItems(userId));
    }

    @DeleteMapping(value = "/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        service.deleteItem(itemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItemByNameAndDesctription(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam String text) {
        return ResponseEntity.ok().body(service.searchItemByNameAndDesctription(userId,text));
    }
}