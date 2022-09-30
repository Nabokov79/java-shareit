package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestBody BookingRequestDto bookingDto) {
        return ResponseEntity.ok().body(bookingService.createBooking(userId, bookingDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> confirmBooking(@PathVariable Long bookingId,
                                                             @RequestParam Boolean approved,
                                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(bookingService.confirmBooking(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBookingById(@PathVariable Long bookingId,
                                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookingsByBookerId(
                                                    @RequestParam(name = "from", defaultValue = "0") int from,
                                                    @RequestParam(name = "size", defaultValue = "10") int size,
                                                    @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(bookingService.getAllBookingsByBookerId(from, size, state, userId));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getAllBookingsOwnerItem(
                                                    @RequestParam(name = "from", defaultValue = "0") int from,
                                                    @RequestParam(name = "size", defaultValue = "10") int size,
                                                    @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok().body(bookingService.getAllBookingsOwnerItem(from, size, state, userId));
    }
}