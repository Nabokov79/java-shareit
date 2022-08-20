package ru.practicum.shareit.booking.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Booking {

    private long id;
    private String name;
    private LocalDate start;
    private LocalDate end;
    private String item;
    private String booker;
    private Status status;

    public Booking(long id, String name, LocalDate start, LocalDate end, String item, String booker, Status status) {
        this.id = id;
        this.name = name;
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }
}
