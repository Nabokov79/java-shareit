package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.model.ItemRequest;

@Component
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto (item.getId(),
                           item.getName(),
                           item.getDescription(),
                           item.getAvailable(),
                           item.getOwner(),
                           item.getRequest() != null ? item.getRequest().getId() : null);
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item (itemDto.getId(),
                         itemDto.getName(),
                         itemDto.getDescription(),
                         itemDto.getAvailable(),
                         itemDto.getOwner(),
                         new ItemRequest());
    }
}