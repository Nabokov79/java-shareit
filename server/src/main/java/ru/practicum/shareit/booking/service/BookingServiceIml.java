package ru.practicum.shareit.booking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceIml implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public BookingServiceIml(BookingRepository bookingRepository,
                             ItemRepository itemRepository,
                             UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public BookingResponseDto createBooking(Long userId, BookingRequestDto bookingDto) {
        Booking booking = BookingMapper.toBooking(bookingDto);
        setBookingValues(userId, booking, bookingDto);
        bookingRepository.save(booking);
        logger.info("Create booking");
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public BookingResponseDto confirmBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                            .orElseThrow(() -> new NotFoundException(String.format("Booking %s not found", bookingId)));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("User no item owner " + booking.getItem().getId());
        }
        if (!approved) {
            if (booking.getStatus().toString().equals("REJECTED")) {
                throw new BadRequestException("Booking bad request. Status REJECTED not set.");
            }
            booking.setStatus(Status.REJECTED);
        } else {
            if (booking.getStatus().toString().equals("APPROVED")) {
                throw new BadRequestException("Booking bad request. Status APPROVED not set.");
            }
            booking.setStatus(Status.APPROVED);
        }
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                            .orElseThrow(() -> new NotFoundException(String.format("Booking %s not found", bookingId)));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException(String.format("User %s not owner booking or item", bookingId));
        }
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByBookerId(int from, int size, String state, Long userId) {
        userService.getUser(userId);
        Pageable pageable = PageRequest.of(from / size,size, Sort.by("start").descending());
        logger.warn("Page = " + pageable.getPageNumber() + " " + "Size = " + pageable.getPageSize());
        List<Booking> bookings =  bookingRepository.findBookingByBookerId(userId, pageable);
        if (bookings.isEmpty()) {
            throw new NotFoundException(String.format("Bookings not found for user %s", userId));
        }
        logger.info("Get all bookings by owner_id = " + userId + " State = " + state);
        return sortByState(state, bookings);
    }

    @Override
    public List<BookingResponseDto> getAllBookingsOwnerItem(int from, int size,String state, Long userId) {
        userService.getUser(userId);
        Pageable pageable = PageRequest.of(from / size,size, Sort.by("start").descending());
        List<Booking> bookings = bookingRepository.findBookingByOwnerId(userId, pageable).stream()
                                .filter(booking -> booking.getStatus() != Status.REJECTED).collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new NotFoundException(String.format("Bookings not found for user %s", userId));
        }
        logger.info("Get all bookings by owner_id = " + userId + " State = " + state);
        return sortByState(state, bookings);
    }

    private void setBookingValues(Long userId, Booking booking, BookingRequestDto bookingDto) {
        booking.setItem(itemRepository.findById(bookingDto.getItemId())
                 .orElseThrow(() -> new NotFoundException(String.format("item %s not found", bookingDto.getItemId()))));

        if (!booking.getItem().getAvailable()) {
            throw new BadRequestException("Item unavailable for booking");
        }
        if (booking.getItem().getOwner().getId() == userId) {
            throw new NotFoundException(String.format("User %s owner this item", bookingDto.getItemId()));
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Probably time booking for user = " + userId);
        }
        UserDto userDb = userService.getUser(userId);
        booking.setBooker(UserMapper.toUser(userDb));
        logger.info("Set bookings parameters by user_id = " + userId);
    }

    private List<BookingResponseDto> sortByState(String state, List<Booking> bookings) {
        logger.info("Get booking type " + state);
        switch (State.valueOf(state)) {
            case FUTURE:
                return bookings.stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case CURRENT:
                return bookings.stream().filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()))
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case REJECTED:
                return bookings.stream().filter(booking -> booking.getStatus().toString().equals("REJECTED"))
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case PAST:
                return bookings.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case WAITING:
                return bookings.stream().filter(booking -> booking.getStatus().toString().equals("WAITING"))
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            default:
                return bookings.stream().map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
        }
    }
}