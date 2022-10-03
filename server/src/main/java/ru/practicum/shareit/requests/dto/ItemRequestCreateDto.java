package ru.practicum.shareit.requests.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
public class ItemRequestCreateDto {

    private long id;
    private String description;
}
