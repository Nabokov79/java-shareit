package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
public class ItemRequestCreateDto {

    private long id;
    @NotBlank(message = "description should not be blank")
    private String description;
}
