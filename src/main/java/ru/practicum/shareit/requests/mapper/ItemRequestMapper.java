package ru.practicum.shareit.requests.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.requests.dto.ItemRequestCreateDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestResponseDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
@Slf4j
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestCreateDto itemRequestCreateDto) {
        return new ItemRequest(itemRequestCreateDto.getId(),
                               itemRequestCreateDto.getDescription(),
                               LocalDateTime.now(),
                               new User());
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                                  itemRequest.getDescription(),
                                  itemRequest.getCreated());
    }

    public static ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest) {
        return new ItemRequestResponseDto(itemRequest.getId(),
                                          itemRequest.getDescription(),
                                          itemRequest.getCreated(),
                                          new ArrayList<>());
    }
}