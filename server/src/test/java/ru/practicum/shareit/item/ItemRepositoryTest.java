package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User user;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        user = userRepository.save(new User(1L, "User1", "user@email.ru"));
        itemRequest =  itemRequestRepository.save(new ItemRequest(1L, "description",
                                                                      LocalDateTime.now(), user));
        item = itemRepository.save(new Item(1L, "item", "item test", true,
                                                                                  user, itemRequest));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void findAllByOwnerId() {
        Pageable pageable = PageRequest.of(0,1);
        List<Item> itemDbList = itemRepository.findAllByOwnerId(user.getId(), pageable);
        assertNotNull(itemDbList);
        assertEquals(1, itemDbList.size());
        Item itemDb = itemDbList.get(0);
        assertEquals(item.getName(), itemDb.getName());
        assertEquals(item.getDescription(), itemDb.getDescription());
        assertEquals(item.getAvailable(), itemDb.getAvailable());
        assertEquals(item.getOwner(), itemDb.getOwner());
        assertEquals(item.getRequest(), itemDb.getRequest());
    }

    @Test
    void findAllByRequestId() {
        List<Item> itemDbList = itemRepository.findAllByRequestId(itemRequest.getId());
        assertNotNull(itemDbList);
        assertEquals(1, itemDbList.size());
        Item itemDb = itemDbList.get(0);
        assertEquals(item.getName(), itemDb.getName());
        assertEquals(item.getDescription(), itemDb.getDescription());
        assertEquals(item.getAvailable(), itemDb.getAvailable());
        assertEquals(item.getOwner(), itemDb.getOwner());
        assertEquals(item.getRequest(), itemDb.getRequest());
    }
}
