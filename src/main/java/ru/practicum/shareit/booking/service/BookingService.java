package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(Long userId, BookingRequestDto bookingDto);

    BookingResponseDto confirmBooking(Long bookingId, Boolean approved, Long userId);

    List<BookingResponseDto> getAllBookingsByBookerId(String state, Long userId);

    BookingResponseDto getBookingById(Long bookingId, Long userId);

    List<BookingResponseDto> getAllBookingsOwnerItem(String state, Long userId);
}
