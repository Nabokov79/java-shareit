package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.model.ItemBooking;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class ItemResponseDto {

    private long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemBooking lastBooking;
    private ItemBooking nextBooking;
    private List<CommentResponseDto> comments;
}