package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Booking {

    private long id;
    private String name;
    private LocalDate start;
    private LocalDate end;
    private String item;
    private User booker;
    private Status status;

    public Booking(long id, String name, LocalDate start, LocalDate end, String item, User booker, Status status) {
        this.id = id;
        this.name = name;
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }
}
