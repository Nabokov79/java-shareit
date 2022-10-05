package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import ru.practicum.shareit.common.Create;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Value
public class UserDto {

    long id;
    @NotBlank(groups = {Create.class}, message = "name should not be blank")
    String name;
    @NotBlank(groups = {Create.class}, message = "email should not be blank")
    @Email(groups = {Create.class}, message = "email is not correct")
    String email;
}
