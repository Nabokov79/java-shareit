package ru.practicum.shareit.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;


@RequestMapping(path = "/users")
@Controller
public class UserController {

    private final UserService service;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }


    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        UserDto userDto1 = service.createUser(userDto);
        logger.error("Return from service user ={}",userDto1);
        return ResponseEntity.ok().body(userDto1);
    }

    @PatchMapping(value = "/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return ResponseEntity.ok().body(service.updateUser(userId, userDto));
    }

    @GetMapping(value = "/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok().body(service.getUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok().body(service.getAllUsers());
    }

    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        service.delete(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}