package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.client.BaseClient;
import java.util.Map;

@Service
@Slf4j
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(Long userId, BookingRequestDto bookingDto) {
        log.info("Received request to create booking from the userId={}, bookingDto={}", userId, bookingDto);
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> confirmBooking(Long bookingId, Boolean approved, Long userId) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        log.info("Received request to confirm booking from the userId={}, bookingId={}, approved={}", userId,
                bookingId,	 approved);
        return patch("/" + bookingId + "?approved=" + approved, userId, parameters);
    }

    public ResponseEntity<Object> getBookingById(Long bookingId, Long userId) {
        log.info("Received request to get booking by id userId={}, bookingId={}", userId, bookingId);
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookingsByBookerId(int from, int size, State state, Long userId) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        log.info("Received request to get all booking by bookerId, state {}, userId={}, from={}, size={}",
                                                                                             state, userId, from, size);
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsOwnerItem(int from, int size, State state, Long userId) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        log.info("Received a request to get all booking by ownerId state {}, userId={}, from={}, size={}",
                                                                                             state, userId, from, size);
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }
}