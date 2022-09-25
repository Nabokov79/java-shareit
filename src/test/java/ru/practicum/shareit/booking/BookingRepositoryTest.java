package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User user;
    private Item item;

    @BeforeEach
    void beforeEach() {
        user = userRepository.save(new User(1L, "user", "user@email.ru"));
        ItemRequest itemRequest = itemRequestRepository.save(new ItemRequest(1L, "description",
                                                                                            LocalDateTime.now(), user));
        item = itemRepository.save(new Item(1L, "item", "item test", true, user,
                                                                                                          itemRequest));
        bookingRepository.save(new Booking(1L, LocalDateTime.now().plusHours(1),
                                                  LocalDateTime.now().plusDays(1), item, user, Status.WAITING));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findBookingByBookerId() {
        Pageable pageable = PageRequest.of(0,1, Sort.by("start").descending());
        List<Booking> bookingList = bookingRepository.findBookingByBookerId(user.getId(), pageable);
        assertNotNull(bookingList);
        assertEquals(1, bookingList.size());
        Booking booking = bookingList.get(0);
        assertEquals(booking.getId(),booking.getId());
        assertEquals(booking.getStart().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
                     booking.getStart().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertEquals(booking.getEnd().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
                     booking.getEnd().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertEquals(user.getName(), booking.getBooker().getName());
        assertEquals(user.getEmail(), booking.getBooker().getEmail());
    }

    @Test
    void findBookingByOwnerId() {
        Pageable pageable = PageRequest.of(0,1, Sort.by("start").descending());
        List<Booking> bookingList = bookingRepository.findBookingByOwnerId(user.getId(), pageable);
        assertNotNull(bookingList);
        assertEquals(1, bookingList.size());
        Booking booking = bookingList.get(0);
        assertEquals(booking.getId(),booking.getId());
        assertEquals(booking.getStart().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
                    booking.getStart().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertEquals(booking.getEnd().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
                     booking.getEnd().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertEquals(user.getName(), booking.getBooker().getName());
        assertEquals(user.getEmail(), booking.getBooker().getEmail());
    }

    @Test
    void findBookingByItemId() {
        List<Booking> bookingList = bookingRepository.findBookingByItemId(item.getId());
        assertNotNull(bookingList);
        assertEquals(1, bookingList.size());
        Booking booking = bookingList.get(0);
        assertEquals(booking.getId(),booking.getId());
        assertEquals(booking.getStart().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
                     booking.getStart().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertEquals(booking.getEnd().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
                     booking.getEnd().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertEquals(user.getName(), booking.getBooker().getName());
        assertEquals(user.getEmail(), booking.getBooker().getEmail());
    }

    @Test
    void findBookingByItemIdAndBookerId() {
        List<Booking> bookingList = bookingRepository.findBookingByItemIdAndBookerId(item.getId(), user.getId());
        assertNotNull(bookingList);
        assertEquals(1, bookingList.size());
        Booking booking = bookingList.get(0);
        assertEquals(booking.getId(),booking.getId());
        assertEquals(booking.getStart().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
                     booking.getStart().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertEquals(booking.getEnd().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
                     booking.getEnd().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertEquals(user.getName(), booking.getBooker().getName());
        assertEquals(user.getEmail(), booking.getBooker().getEmail());
    }
}