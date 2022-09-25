package ru.practicum.shareit.booking.dto;

import lombok.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {

    private long id;
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}