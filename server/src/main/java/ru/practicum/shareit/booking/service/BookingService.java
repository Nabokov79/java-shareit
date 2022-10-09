package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(Long userId, BookingRequestDto bookingDto);

    BookingResponseDto confirmBooking(Long bookingId, Boolean approved, Long userId);

    List<BookingResponseDto> getAllBookingsByBookerId(int from, int size, String state, Long userId);

    BookingResponseDto getBookingById(Long bookingId, Long userId);

    List<BookingResponseDto> getAllBookingsOwnerItem(int from, int size, String state, Long userId);
}

