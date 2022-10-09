package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.requests.dto.ItemRequestCreateDto;
import java.util.Map;

@Service
@Slf4j
public class ItemRequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestCreateDto itemRequest) {
        log.info("Received request to create request from the userId={}, itemRequest={}", userId, itemRequest);
        return post("",userId, itemRequest);
    }

    public ResponseEntity<Object> getUserRequests(Long userId) {
        log.info("Received request to get request by userId from the userId={}", userId);
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequest(int from, int size, Long userId) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        log.info("Received request to get all request from the userId={}, from={}, size={}", userId, from, size);
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getRequest(Long requestId, Long userId) {
        log.info("Received request to get request from by requestId the userId={}, requestId={}", userId, requestId);
        return get("/" + requestId, userId);
    }
}
