package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceIml;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImlTest {

    private BookingRepository bookingRepository;
    private BookingServiceIml bookingService;
    private ItemRepository itemRepository;
    private UserService userService;
    private Booking booking;
    private BookingRequestDto bookingRequestDto;
    private UserDto userDto;
    private User user;
    private Item item;

    @BeforeEach
    void beforeEach() {
       userDto = new UserDto(2L, "user", "user@email.ru");
       user = new User(1L, "user", "user@email.ru");
       item = new Item(1L, "item", "item test", true, user, new ItemRequest());
       booking = new Booking(1L, LocalDateTime.now().plusHours(1),
                                    LocalDateTime.now().plusDays(1),
                                    item,user, Status.WAITING);
       bookingRequestDto = new BookingRequestDto(1L, 1L, LocalDateTime.now().plusHours(1),
                                                                   LocalDateTime.now().plusDays(1));
       bookingRepository = mock(BookingRepository.class);
       userService = mock(UserService.class);
       itemRepository = mock(ItemRepository.class);
       bookingService = new BookingServiceIml(bookingRepository,itemRepository,userService);
    }

    @Test
    void createBooking() {
        when(userService.getUser(userDto.getId())).thenReturn(userDto);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);
        BookingResponseDto bookingResponseDto = bookingService.createBooking(2L, bookingRequestDto);
        assertEquals(booking.getId(),bookingResponseDto.getId());
        assertEquals(booking.getStart().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
          bookingResponseDto.getStart().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertEquals(booking.getEnd().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
          bookingResponseDto.getEnd().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertEquals(user.getName(), bookingResponseDto.getBooker().getName());
        assertEquals(user.getEmail(), bookingResponseDto.getBooker().getEmail());
        assertEquals(item, bookingResponseDto.getItem());
        assertEquals("WAITING", bookingResponseDto.getStatus());
        Item item2 = new Item(1L, "item", "item test", false, user, new ItemRequest());
        BookingRequestDto booking1 = new BookingRequestDto(1L, item2.getId(), LocalDateTime.now().minusDays(1),
                                                                                LocalDateTime.now().plusHours(1));
        BookingRequestDto booking2 = new BookingRequestDto(1L, item2.getId(), LocalDateTime.now().plusHours(2),
                                                                                LocalDateTime.now().plusHours(1));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item2));
        assertThrows(BadRequestException.class, () -> bookingService.createBooking(2L, bookingRequestDto));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(1L, bookingRequestDto));
        assertThrows(BadRequestException.class, () -> bookingService.createBooking(2L, booking1));
        final  var thrown = assertThrows(BadRequestException.class,
                                                                () -> bookingService.createBooking(2L, booking2));
        assertEquals("Probably time booking for user = 2", thrown.getMessage());
    }

    @Test
    void confirmBooking() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        BookingResponseDto bookingDb = bookingService.confirmBooking(booking.getId(), false, 1L);
        assertEquals("REJECTED", bookingDb.getStatus());
        assertThrows(BadRequestException.class, () -> bookingService.confirmBooking(booking.getId(), false, 1L));
        BookingResponseDto bookingDb2 = bookingService.confirmBooking(booking.getId(), true, 1L);
        assertEquals("APPROVED", bookingDb2.getStatus());
        assertThrows(BadRequestException.class, () -> bookingService.confirmBooking(booking.getId(), true, 1L));
        assertThrows(NotFoundException.class, () -> bookingService.confirmBooking(booking.getId(), true, 2L));
        assertThrows(NotFoundException.class, () -> bookingService.confirmBooking(2L, false, 1L));
    }

    @Test
    void getBookingById() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        BookingResponseDto bookingResponseDto = bookingService.getBookingById(booking.getId(), 1L);
        assertEquals(booking.getId(),bookingResponseDto.getId());
        assertEquals(booking.getStart().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
          bookingResponseDto.getStart().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertEquals(booking.getEnd().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
          bookingResponseDto.getEnd().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertEquals(user.getName(), bookingResponseDto.getBooker().getName());
        assertEquals(user.getEmail(), bookingResponseDto.getBooker().getEmail());
        assertEquals(item, bookingResponseDto.getItem());
        assertEquals("WAITING", bookingResponseDto.getStatus());
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(booking.getId(), 2L));
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(2L, 1L));
    }

    @Test
    void getAllBookingsByBookerId() {
        Pageable pageable = PageRequest.of(1,1, Sort.by("start").descending());
        when(userService.getUser(userDto.getId())).thenReturn(userDto);
        when(bookingRepository.findBookingByBookerId(userDto.getId(), pageable))
                              .thenReturn(List.of(booking));
        List<BookingResponseDto> bookingResponseDtos
                                 = bookingService.getAllBookingsByBookerId(1, 1, "ALL", userDto.getId());
        assertNotNull(bookingResponseDtos);
        assertEquals(1, bookingResponseDtos.size());
        BookingResponseDto bookingResponseDto = bookingResponseDtos.get(0);
        assertEquals(booking.getId(),bookingResponseDto.getId());
        assertEquals(booking.getStart().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
          bookingResponseDto.getStart().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertEquals(booking.getEnd().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
          bookingResponseDto.getEnd().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertEquals(user.getName(), bookingResponseDto.getBooker().getName());
        assertEquals(user.getEmail(), bookingResponseDto.getBooker().getEmail());
        assertEquals(item, bookingResponseDto.getItem());
        assertEquals("WAITING", bookingResponseDto.getStatus());
        List<BookingResponseDto> bookingByStateFUTURE
                              = bookingService.getAllBookingsByBookerId(1, 1, "FUTURE", userDto.getId());
        assertNotNull(bookingByStateFUTURE);
        assertEquals(1, bookingByStateFUTURE.size());
        when(bookingRepository.findBookingByBookerId(userDto.getId(), pageable))
                            .thenReturn(List.of(new Booking(1L, LocalDateTime.now().minusHours(1),
                                                           LocalDateTime.now().plusDays(1),item,user, Status.REJECTED)));
        List<BookingResponseDto> bookingByStateCURRENT
                             = bookingService.getAllBookingsByBookerId(1, 1, "CURRENT", userDto.getId());
        assertNotNull(bookingByStateCURRENT);
        assertEquals(1, bookingByStateCURRENT.size());
        when(bookingRepository.findBookingByBookerId(userDto.getId(), pageable))
                             .thenReturn(List.of(new Booking(1L, LocalDateTime.now().plusHours(1),
                                                          LocalDateTime.now().plusDays(1),item,user, Status.REJECTED)));
        List<BookingResponseDto> bookingByStateREJECTED
                            = bookingService.getAllBookingsByBookerId(1, 1, "REJECTED", userDto.getId());
        assertNotNull(bookingByStateREJECTED);
        assertEquals(1, bookingByStateREJECTED.size());
        when(bookingRepository.findBookingByBookerId(userDto.getId(), pageable))
                             .thenReturn(List.of(new Booking(1L, LocalDateTime.now().minusHours(2),
                                                         LocalDateTime.now().minusHours(1),item,user, Status.WAITING)));
        List<BookingResponseDto> bookingByStatePAST
                                = bookingService.getAllBookingsByBookerId(1, 1, "PAST", userDto.getId());
        assertNotNull(bookingByStatePAST);
        assertEquals(1, bookingByStatePAST.size());
        when(bookingRepository.findBookingByBookerId(userDto.getId(), pageable))
                              .thenReturn(List.of(booking));
        List<BookingResponseDto> bookingByStateWAITING
                             = bookingService.getAllBookingsByBookerId(1, 1, "WAITING", userDto.getId());
        assertNotNull(bookingByStateWAITING);
        assertEquals(1, bookingByStateWAITING.size());
        assertThrows(NotFoundException.class,
                                   () -> bookingService.getAllBookingsByBookerId(1, 1, "ALL", 1L));
    }

    @Test
    void getAllBookingsOwnerItem() {
        Pageable pageable = PageRequest.of(1,1, Sort.by("start").descending());
        when(userService.getUser(userDto.getId())).thenReturn(userDto);
        when(bookingRepository.findBookingByOwnerId(userDto.getId(), pageable))
                              .thenReturn(List.of(booking));
        List<BookingResponseDto> bookingResponseDtos
                                  = bookingService.getAllBookingsOwnerItem(1, 1, "ALL", userDto.getId());
        assertNotNull(bookingResponseDtos);
        assertEquals(1, bookingResponseDtos.size());
        BookingResponseDto bookingResponseDto = bookingResponseDtos.get(0);
        assertEquals(booking.getId(),bookingResponseDto.getId());
        assertEquals(booking.getStart().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
          bookingResponseDto.getStart().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertEquals(booking.getEnd().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")),
          bookingResponseDto.getEnd().format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")));
        assertEquals(user.getName(), bookingResponseDto.getBooker().getName());
        assertEquals(user.getEmail(), bookingResponseDto.getBooker().getEmail());
        assertEquals(item, bookingResponseDto.getItem());
        assertEquals("WAITING", bookingResponseDto.getStatus());
        assertThrows(NotFoundException.class,
                                    () -> bookingService.getAllBookingsOwnerItem(1, 1, "ALL", 1L));
    }
}
