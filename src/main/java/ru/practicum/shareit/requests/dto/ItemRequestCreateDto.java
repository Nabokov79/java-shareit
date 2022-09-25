package ru.practicum.shareit.requests.dto;

import lombok.*;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestCreateDto {

    private long id;
    @NotBlank(message = "description should not be blank")
    private String description;
}
