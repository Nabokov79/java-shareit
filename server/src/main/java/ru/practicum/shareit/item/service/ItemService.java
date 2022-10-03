package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Long userId);

    CommentResponseDto createComment(CommentRequestDto commentDto, Long itemId, Long userId);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemResponseDto getItem(Long userId, Long itemId);

    List<ItemResponseDto> getAllItems(int from, int size, Long userId);

    void deleteItem(Long itemId);

    List<ItemDto> searchItemByNameAndDesctription(Long userId, String text);
}