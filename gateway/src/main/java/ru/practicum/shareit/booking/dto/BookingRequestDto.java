package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class BookingRequestDto {

    private long id;
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}