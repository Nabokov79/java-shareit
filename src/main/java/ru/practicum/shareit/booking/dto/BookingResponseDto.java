package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class BookingResponseDto {

    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private String status;
    private User booker;
    private Item item;
}