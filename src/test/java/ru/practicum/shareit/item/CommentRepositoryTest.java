package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User user;
    private Item item;
    private ItemRequest itemRequest;
    private Comment comment;

    @BeforeEach
    void beforeEach() {
        user = userRepository.save(new User(1L, "User1", "user@email.ru"));
        itemRequest =  itemRequestRepository.save(new ItemRequest(1L, "description", LocalDateTime.now(), user));
        item = itemRepository.save(new Item(1L, "item", "item test", true, user, itemRequest));
        comment = commentRepository.save(new Comment(1L, "Comment test", item,user));
    }

    @Test
    void findAllByItemId() {
        List<Comment> commentDbList = commentRepository.findAllByItemId(item.getId());
        assertNotNull(commentDbList);
        assertEquals(1, commentDbList.size());
        Comment commentDb = commentDbList.get(0);
        assertEquals(comment.getId(),commentDb.getId());
        assertEquals(comment.getText(), commentDb.getText());
        assertEquals(comment.getItem(), commentDb.getItem());
        assertEquals(comment.getAuthor(), commentDb.getAuthor());
    }
}