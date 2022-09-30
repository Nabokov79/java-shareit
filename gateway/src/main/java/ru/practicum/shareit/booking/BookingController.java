package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
												@RequestBody BookingRequestDto bookingDto) {
		log.info("Received request to create booking from the userId={}, bookingDto={}", userId, bookingDto);
		return bookingClient.createBooking(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> confirmBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
												 @PathVariable Long bookingId,
												 @RequestParam Boolean approved) {
		log.info("Received request to update booking from the userId={}, bookingId={}, approved={}", userId,
																							 	 bookingId,	 approved);
		return bookingClient.confirmBooking(bookingId, approved, userId);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId,
												 @RequestHeader("X-Sharer-User-Id") Long userId) {
		log.info("Received request to get booking from the userId={}, bookingId={}", userId, bookingId);
		return bookingClient.getBookingById(bookingId, userId);
	}

	@GetMapping
	public ResponseEntity<Object> getAllBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") Long userId,
											@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
											@Positive @RequestParam(name = "size", defaultValue = "20")	int size,
											@RequestParam(value = "state", defaultValue = "ALL") String stateParam) {
		State state = State.from(stateParam)
				.orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
		log.info("Received request to get all booking by bookerId, state {}, userId={}, from={}, size={}",
																						stateParam, userId, from, size);
		return bookingClient.getAllBookingsByBookerId(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getAllBookingsOwnerItem(@RequestHeader("X-Sharer-User-Id") Long userId,
											@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
											@Positive @RequestParam(name = "size", defaultValue = "20")	int size,
											@RequestParam(value = "state", defaultValue = "ALL") String stateParam) {
		State state = State.from(stateParam)
				.orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
		log.info("Received a request to get all booking by ownerId state {}, userId={}, from={}, size={}",
																		  stateParam, userId, from, size);
		return bookingClient.getAllBookingsOwnerItem(userId, state, from, size);
	}
}