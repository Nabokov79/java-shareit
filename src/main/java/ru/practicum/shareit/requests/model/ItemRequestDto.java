package ru.practicum.shareit.requests.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private long id;
    private String description;
    private User requestor;
    private LocalDate created;
}
