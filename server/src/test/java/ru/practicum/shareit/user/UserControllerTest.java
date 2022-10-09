package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import java.util.Collections;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    private UserDto userDto;
    private UserDto userDto2;

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto(1L, "User1", "user@email.ru");
        userDto2 = new UserDto(1L, "User1", "mail@email.ru");
    }

    @Test
    void createUser() throws Exception {
        when(userService.createUser(any())).thenReturn(userDto);
        String body = mapper.writeValueAsString(userDto);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                        .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                        .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
    }

    @Test
    void updateUser() throws Exception {
        when(userService.updateUser(1L, userDto2)).thenReturn(userDto2);
        String body2 = mapper.writeValueAsString(userDto2);
        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body2))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name", is(userDto2.getName()), String.class))
                        .andExpect(jsonPath("$.email", is(userDto2.getEmail()), String.class));
    }

    @Test
    void getUser() throws Exception {
        when(userService.getUser(1L)).thenReturn(userDto);
        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                        .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
    }

    @Test
    void getUser2() throws Exception {
        when(userService.getUser(1L)).thenThrow(NotFoundException.class);
        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(userDto));
        MvcResult result = mockMvc.perform(get("/users")
                                  .contentType(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isOk())
                                  .andReturn();
        assertNotNull(result);
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }
}