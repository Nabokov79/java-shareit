package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.requests.dto.ItemRequestCreateDto;
import ru.practicum.shareit.requests.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = { "db.name=test"})
@SpringJUnitConfig({ShareItApp.class, ItemServiceImpl.class})
public class ItemServiceIntegrationTest {

    private final EntityManager em;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final ItemRequestServiceImpl itemRequestService;

    @Test
    void createItem() {
        UserDto userDto = new UserDto(1L, "user", "user@email.ru");
        userService.createUser(userDto);
        ItemRequestCreateDto itemRequestDto = new ItemRequestCreateDto(1L, "description");
        itemRequestService.createRequest(userDto.getId(), itemRequestDto);
        ItemDto itemDto = new ItemDto(1L, "item", "item test", true, 1L,1L);
        itemService.createItem(itemDto, 1L);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = : id", Item.class);
        Item item = query.setParameter("id",itemDto.getId()).getSingleResult();
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(),equalTo(itemDto.getDescription()));
        assertThat(item.getOwner().getId(), equalTo(userDto.getId()));
        assertThat(item.getOwner().getName(), equalTo(userDto.getName()));
        assertThat(item.getOwner().getEmail(), equalTo(userDto.getEmail()));
        assertThat(item.getRequest().getId(), equalTo(itemRequestDto.getId()));
        assertThat(item.getRequest().getDescription(), equalTo(itemRequestDto.getDescription()));
    }
}