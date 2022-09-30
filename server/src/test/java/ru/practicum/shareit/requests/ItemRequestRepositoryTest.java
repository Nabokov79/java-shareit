package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;

    private ItemRequest itemRequest;
    private User user;

    @BeforeEach
    void beforeEach() {
        user = userRepository.save(new User(1L, "User1", "user@email.ru"));
        itemRequest = itemRequestRepository.save(new ItemRequest(1L,"findAllByRequesterId",
                LocalDateTime.now(), user));
    }

    @Test
    void findAllByRequesterId() {
        List<ItemRequest> itemRequestsList = itemRequestRepository.findAllByRequesterId(user.getId());
        assertNotNull(itemRequestsList);
        assertEquals(1, itemRequestsList.size());
        ItemRequest itemRequestDb = itemRequestsList.get(0);
        assertEquals(itemRequest.getId(), itemRequestDb.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDb.getDescription());
        assertEquals(itemRequest.getCreated().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
                                itemRequestDb.getCreated().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertEquals(user.getId(),itemRequestDb.getRequester().getId());
        assertEquals(user.getName(),itemRequestDb.getRequester().getName());
        assertEquals(user.getEmail(),itemRequestDb.getRequester().getEmail());
    }
}