package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class CommentResponseDto {

    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}