package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.common.Create;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
public class CommentRequestDto {

    private long id;
    @NotBlank(groups = {Create.class}, message = "email should not be blank")
    private String text;
}