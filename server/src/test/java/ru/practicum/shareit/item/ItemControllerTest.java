package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.ItemBooking;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private ItemServiceImpl itemService;
    private UserRepository userRepository;
    private ItemResponseDto itemResponseDto;

    private ItemDto itemDto;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private CommentRequestDto comment;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        itemDto = new ItemDto(1L, "item", "item test", true, 1L, 1L);
        itemDto1 = new ItemDto(1L, "", "item test", true, 1L, 1L);
        itemDto2 = new ItemDto(1L, "item", "", true, 1L, 1L);
        comment = new CommentRequestDto(1L,"Comment for item");
        itemResponseDto = new ItemResponseDto(1L, "item","Request", true,
                         new ItemBooking(1L,1L), new ItemBooking(2L,2L), List.of());
    }

    @Test
    void createItem() throws Exception {
        User user = new User(1L, "user", "user@email.ru");
        when(userRepository.save(user)).thenReturn(user);
        userRepository.save(user);
        when(itemService.createItem(itemDto, 1L)).thenReturn(itemDto);
        String body = mapper.writeValueAsString(itemDto);
        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-Sharer-User-Id", "1"))
                        .andExpect(status().isOk());

    }

    @Test
    void createComment() throws Exception {
        String body = mapper.writeValueAsString(comment);
        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-Sharer-User-Id", "1"))
                        .andExpect(status().isOk());
    }

    @Test
    void updateItem() throws Exception {
        User user = new User(1L, "user", "user@email.ru");
        when(userRepository.save(user)).thenReturn(user);
        when(itemService.updateItem(eq(1L), eq(1L), any())).thenReturn(itemDto);
        String body = mapper.writeValueAsString(itemDto);
        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("X-Sharer-User-Id", "1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                        .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                        .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class));
    }

    @Test
    void getItem() throws Exception {
        when(itemService.createItem(itemDto, 1L)).thenReturn(itemDto);
        when(itemService.getItem(1L, 1L)).thenReturn(itemResponseDto);
        mockMvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id", is(itemResponseDto.getId()), Long.class))
                        .andExpect(jsonPath("$.name", is(itemResponseDto.getName()), String.class))
                        .andExpect(jsonPath("$.description", is(itemResponseDto.getDescription()), String.class));
    }

    @Test
    void getAllItems() throws Exception {
        when(itemService.getAllItems(1,1,1L)).thenReturn(Collections.singletonList(itemResponseDto));
        mockMvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "1")
                        .param("size", "1"))
                        .andExpect(status().isOk());
    }

    @Test
    void deleteItem() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void searchItemByNameAndDesctription() throws Exception {
        when(itemService.searchItemByNameAndDesctription(1L,"item")).thenReturn(Collections.singletonList(itemDto));
        mockMvc.perform(get("/items/search?text=test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                        .andExpect(status().isOk());
    }
}