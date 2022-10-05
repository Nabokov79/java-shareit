package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.Map;

@Service
@Slf4j
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(ItemDto itemDto, Long userId) {
        log.info("Received request to create item from the userId={}, itemDto={}", userId, itemDto);
        return post("", userId,itemDto);
    }

    public ResponseEntity<Object> createComment(CommentRequestDto commentDto, Long itemId, Long userId) {
        log.info("Received request to create comment from the userId={}, commentDto={}", userId, commentDto);
        return post("/" + itemId + "/comment", userId, commentDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Received request to update item from the itemId={}, userId={}, itemDto={}", itemId, userId, itemDto);
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        log.info("Received request to get item from the userId={}, itemId={}", userId, itemId);
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItems(int from, int size, Long userId) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        log.info("Received request to get all items userId={}, from={}, size={}", userId, from, size);
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> deleteItem(Long itemId) {
        return delete("/" + itemId);
    }

    public ResponseEntity<Object> searchItemByNameAndDesctription(Long userId, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        log.info("Received request to search items by name and desctription userId={}, text={}", userId, text);
        return get("/search?text={text}", userId, parameters);
    }
}