package ru.practicum.shareit.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.Create;
import java.util.List;

@RequestMapping(path = "/users")
@RestController
public class UserController {

    private final UserService service;


    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }


    @PostMapping
    public ResponseEntity<UserDto> createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        return ResponseEntity.ok().body(service.createUser(userDto));
    }

    @PatchMapping(value = "/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable long userId,
                                              @Validated({Update.class})
                                              @RequestBody UserDto userDto) {
        return ResponseEntity.ok().body(service.updateUser(userId, userDto));
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable long userId) {
        return ResponseEntity.ok().body(service.getUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok().body(service.getAllUsers());
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable long userId) {
        service.delete(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}