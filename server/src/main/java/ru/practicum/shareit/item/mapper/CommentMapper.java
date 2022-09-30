package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    public static Comment toComment(CommentRequestDto commentRequestDto) {
        return new Comment(commentRequestDto.getId(),
                           commentRequestDto.getText(),
                           new Item(),
                           new User());
    }

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        return new CommentResponseDto(comment.getId(),
                                      comment.getText(),
                                      comment.getAuthor().getName(),
                                      LocalDateTime.now());
    }
}