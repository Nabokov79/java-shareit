package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestCreateDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestResponseDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.requests.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemRequestServiceImplTest {

    private ItemRequestRepository itemRequestRepository;
    private UserService userService;
    private ItemRepository itemRepository;
    private ItemRequestServiceImpl itemRequestService;
    private ItemRequest itemRequest;
    private ItemRequestCreateDto itemRequestCreateDto;
    private UserDto userDto;
    private Item item;

    @BeforeEach
    void beforeEach() {
        userService = mock(UserService.class);
        itemRepository = mock(ItemRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService, itemRepository);
        userDto = new UserDto(1L, "User1", "user@email.ru");
        itemRequest = new ItemRequest(1L, "Request",LocalDateTime.now(), UserMapper.toUser(userDto));
        itemRequestCreateDto = new ItemRequestCreateDto(1L, "Request");
        item = new Item(1L, "item", "item test", true,
                                                                               UserMapper.toUser(userDto), itemRequest);
    }

    @Test
    void createRequest() {
        when(userService.getUser(userDto.getId())).thenReturn(userDto);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        ItemRequestDto itemRequestDb = itemRequestService.createRequest(1L, itemRequestCreateDto);
        assertEquals(itemRequestCreateDto.getId(), itemRequestDb.getId());
        assertEquals(itemRequestCreateDto.getDescription(), itemRequestDb.getDescription());
        assertEquals(LocalDateTime.now().format((DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"))),
                               itemRequestDb.getCreated().format((DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"))));
    }

    @Test
    void getUserRequests() {
        when(itemRequestRepository.findAllByRequesterId(userDto.getId()))
                                  .thenReturn(Collections.singletonList(itemRequest));
        when(itemRepository.findAllByRequestId(userDto.getId())).thenReturn(Collections.singletonList(item));
        List<ItemRequestResponseDto> itemRequestList = itemRequestService.getUserRequests(userDto.getId());
        assertNotNull(itemRequestList);
        assertEquals(1,itemRequestList.size());
        ItemRequestResponseDto itemRequestDb = itemRequestList.get(0);
        assertEquals(itemRequest.getId(), itemRequestDb.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDb.getDescription());
        assertEquals(itemRequest.getCreated().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
                                 itemRequestDb.getCreated().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertNotNull(itemRequestDb.getItems());
        assertEquals(1L,itemRequestDb.getItems().size());
    }

    @Test
    void getAllRequest() {
        User user2 = new User(2L, "User1", "user@email.ru");
        Item itemDb = new Item(1L, "item", "item test", true, user2, itemRequest);
        when(itemRequestRepository.findAll(PageRequest.of(1,1))).thenReturn(new PageImpl<>(List.of(itemRequest)));
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(itemDb));
        List<ItemRequestResponseDto> itemRequestList = itemRequestService.getAllRequest(1, 1,2L);
        assertNotNull(itemRequestList);
        assertEquals(1,itemRequestList.size());
        ItemRequestResponseDto itemRequestDb = itemRequestList.get(0);
        assertNotNull(itemRequestDb.getItems());
        assertEquals(itemRequest.getId(), itemRequestDb.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDb.getDescription());
        assertEquals(itemRequest.getCreated().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
                itemRequestDb.getCreated().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertNotNull(itemRequestDb.getItems());
        assertEquals(1, itemRequestDb.getItems().size());
        assertThrows(BadRequestException.class, () -> itemRequestService.getAllRequest(1, 0,2L));
    }

    @Test
    void getRequest() {
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        ItemRequestResponseDto itemRequestDb = itemRequestService.getRequest(itemRequest.getId(), userDto.getId());
        assertEquals(itemRequest.getId(), itemRequestDb.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDb.getDescription());
        assertEquals(itemRequest.getCreated().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
                                 itemRequestDb.getCreated().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
    }
}
