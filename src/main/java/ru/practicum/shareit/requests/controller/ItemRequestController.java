package ru.practicum.shareit.requests.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestCreateDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestResponseDto;
import ru.practicum.shareit.requests.service.ItemRequestService;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestBody
                                                        @Validated ItemRequestCreateDto itemRequestCreateDto) {
        ItemRequestDto itemRequestResponseDto = itemRequestService.createRequest(userId, itemRequestCreateDto);
        return ResponseEntity.ok().body(itemRequestResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestResponseDto>> getUserRequests(
                                                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(itemRequestService.getUserRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestResponseDto>> getAllRequest(@PositiveOrZero
                                                                      @RequestParam(name = "from", defaultValue = "0")
                                                                       int from,
                                                                      @Positive
                                                                      @RequestParam(name = "size", defaultValue = "20")
                                                                      int size,
                                                                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(itemRequestService.getAllRequest(from, size, userId));
    }

    @GetMapping("{requestId}")
    public ResponseEntity<ItemRequestResponseDto> getRequest(@PathVariable Long requestId,
                                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(itemRequestService.getRequest(requestId, userId));
    }
}