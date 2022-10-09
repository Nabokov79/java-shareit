package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exeption.BadRequestException;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestBody BookingRequestDto bookingDto) {
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmBooking(@PathVariable Long bookingId, @RequestParam Boolean approved,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.confirmBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                              @Positive @RequestParam(name = "size", defaultValue = "20") int size,
                                              @RequestParam(value = "state", defaultValue = "ALL") String stateParam) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
        return bookingClient.getAllBookingsByBookerId(from, size, state, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsOwnerItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                             @Positive @RequestParam(name = "size", defaultValue = "20") int size,
                                             @RequestParam(value = "state", defaultValue = "ALL") String stateParam) {
        State state = State.from(stateParam)
                .orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
        return bookingClient.getAllBookingsOwnerItem(from, size, state, userId);
    }
}