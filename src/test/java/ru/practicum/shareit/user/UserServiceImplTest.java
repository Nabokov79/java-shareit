package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserServiceImpl userService;
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        user = new User(1L, "user", "user@email.ru");
    }

    @Test
    void createUser() {
        UserDto userDb = new UserDto(1L, "user", "user@email.ru");
        when(userRepository.save(any())).thenReturn(user);
        UserDto userDto = userService.createUser(userDb);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void updateUser() {
        UserDto userDto = new UserDto(1L, "user", "mail@email.ru");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        UserDto userDtoDb = userService.updateUser(1L, userDto);
        assertEquals(userDtoDb.getId(), userDto.getId());
        assertEquals(userDtoDb.getName(), userDto.getName());
        assertEquals(userDtoDb.getEmail(), userDto.getEmail());
        assertThrows(NotFoundException.class, () -> userService.updateUser(2L,userDto));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDto userDto = userService.getUser(1L);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
        final  var thrown = assertThrows(NotFoundException.class, () -> userService.getUser(2L));
        assertEquals("user 2 not found", thrown.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getAllUsers() {
        assertThrows(NotFoundException.class, () -> userService.getAllUsers());
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        final List<UserDto> userDtoList = userService.getAllUsers();
        assertNotNull(userDtoList);
        assertEquals(1,userDtoList.size());
        UserDto userDto = userDtoList.get(0);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    void delete() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        userService.delete(user.getId());
        assertThrows(NotFoundException.class, () -> userService.delete(2L));
    }
}