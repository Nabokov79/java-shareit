package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    private ItemRepository itemRepository;
    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private ItemRequestRepository itemRequestRepository;
    private ItemServiceImpl itemService;
    private ItemDto itemDto;
    private Item item;
    private User user;
    private Booking booking;
    private Booking booking2;
    private Booking lastBooking;
    private Booking nextBooking;
    private ItemRequest itemRequest;
    private Comment comment;
    private CommentRequestDto commentRequestDto;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemService = new ItemServiceImpl(itemRepository,
                                          commentRepository,
                                          userRepository,
                                          bookingRepository,
                                          itemRequestRepository);
        user = new User(1L, "user", "user@email.ru");
        item = new Item(1L, "item", "item test", true, user, new ItemRequest());
        itemDto = new ItemDto(1L, "item", "item test", true, 1L,1L);
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(5), item, user, Status.WAITING);
        booking2 = new Booking(2L, LocalDateTime.now().minusDays(2),
                                      LocalDateTime.now().minusDays(1).plusHours(5),
                                      item, user, Status.APPROVED);
        lastBooking = new Booking(4L, LocalDateTime.now().minusDays(1),
                                         LocalDateTime.now().minusHours(5),
                                         item, user, Status.APPROVED);
        nextBooking = new Booking(5L, LocalDateTime.now().plusHours(1),
                                         LocalDateTime.now().plusDays(1),
                                         item, user, Status.APPROVED);
        comment = new Comment(1L, "Comment", item,user);
        commentRequestDto = new CommentRequestDto(1L, "CommentRequestDto");
        itemRequest = new ItemRequest(1L, "description", LocalDateTime.now(), user);
    }

    @Test
    void createItem() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto itemDb = itemService.createItem(itemDto, 1L);
        assertEquals(itemDb.getId(), itemDto.getId());
        assertEquals(itemDb.getName(), itemDto.getName());
        assertEquals(itemDb.getDescription(), itemDto.getDescription());
        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, 2L));
    }

    @Test
    void createItemRequestIdIsNull() {
        ItemDto itemDto = new ItemDto(1L, "item", "item test", true, 1L,null);
        Item item = new Item(1L, "item", "item test", true, user,null);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any())).thenReturn(item);
        assertNull(itemService.createItem(itemDto, 1L).getRequestId());
    }

    @Test
    void createComment() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByItemIdAndBookerId(item.getId(), user.getId()))
                              .thenReturn(Collections.singletonList(booking2));
        when(commentRepository.save(comment)).thenReturn(comment);
        CommentResponseDto comment1 = itemService.createComment(commentRequestDto, 1L, 1L);
        assertEquals(commentRequestDto.getId(), comment1.getId());
        assertEquals(commentRequestDto.getText(), comment1.getText());
        assertEquals(user.getName(), comment1.getAuthorName());
        assertEquals(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
                                      comment1.getCreated().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertThrows(BadRequestException.class, () -> itemService.createComment(commentRequestDto, 1L, 2L));
    }

    @Test
    void updateItem() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        ItemDto itemDb = itemService.updateItem(1L, 1L, itemDto);
        assertEquals(item.getId(), itemDb.getId());
        assertEquals(item.getName(), itemDb.getName());
        assertEquals(item.getDescription(), itemDb.getDescription());
        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, 2L, itemDto));
        assertThrows(NotFoundException.class, () -> itemService.updateItem(2L, 1L, itemDto));
    }

    @Test
    void getItem() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItemId(item.getId())).thenReturn(List.of(lastBooking, nextBooking));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        ItemResponseDto itemResponseDto = itemService.getItem(1L, 1L);
        assertEquals(item.getId(), itemResponseDto.getId());
        assertEquals(item.getName(), itemResponseDto.getName());
        assertEquals(item.getDescription(), itemResponseDto.getDescription());
        assertNotNull(itemResponseDto.getLastBooking());
        assertNotNull(itemResponseDto.getNextBooking());
        assertThrows(NotFoundException.class, () -> itemService.getItem(1L, 2L));
        ItemResponseDto itemResponseDto2 = itemService.getItem(2L, 1L);
        assertNull(itemResponseDto2.getLastBooking());
        assertNull(itemResponseDto2.getNextBooking());
    }

    @Test
    void getAllItems() {
        Pageable pageable = PageRequest.of(1,1);
        Item item = new Item(1L, "item", "item test", true, user, null);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(1, pageable)).thenReturn(Collections.singletonList(item));
        when(bookingRepository.findBookingByItemId(1L)).thenReturn(List.of(lastBooking, nextBooking));
        final List<ItemResponseDto> itemDtoList = itemService.getAllItems(1, 1, 1L);
        assertNotNull(itemDtoList);
        assertEquals(1, itemDtoList.size());
        ItemResponseDto itemDto = itemDtoList.get(0);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertNotNull(itemDto.getLastBooking());
        assertNotNull(itemDto.getNextBooking());
        assertThrows(NotFoundException.class, () -> itemService.getAllItems(1, 1, 2L));
    }

    @Test
    void deleteItem() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.ofNullable(item));
        itemService.deleteItem(item.getId());
        assertThrows(NotFoundException.class, () -> itemService.deleteItem(2L));
    }

    @Test
    void searchItemByNameAndDesctription() {
        assertThrows(BadRequestException.class,
                                              () -> itemService.searchItemByNameAndDesctription(1L, "item"));
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));
        List<ItemDto> itemDtoList = itemService.searchItemByNameAndDesctription(1L, "item");
        assertNotNull(itemDtoList);
        assertEquals(1, itemDtoList.size());
        ItemDto itemDto = itemDtoList.get(0);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(0,itemService.searchItemByNameAndDesctription(1L, "").size());
        assertThrows(NotFoundException.class, () -> itemService.getAllItems(1, 1, 2L));
        assertNotNull(itemService.searchItemByNameAndDesctription(1L, "test"));
    }
}