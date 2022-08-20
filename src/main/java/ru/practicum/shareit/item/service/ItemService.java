package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDto getItem(long itemId);

    List<ItemDto> getAllItems(Long userId);
    void deleteItem(long itemId);
    List<ItemDto> searchItem(Long userId, String text);
}