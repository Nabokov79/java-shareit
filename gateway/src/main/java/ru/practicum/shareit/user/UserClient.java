package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Service
@Slf4j
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        log.info("Received request to create users userDto={}", userDto);
        return post("", userDto);
    }

    public ResponseEntity<Object> updateUser(long userId, UserDto userDto) {
        log.info("Received request update user with userId={}, userDto={}", userId, userDto);
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> getUser(long userId) {
        log.info("Received request to get user with userId={}", userId);
        return get("/" + userId);
    }

    public ResponseEntity<Object> getAllUsers() {
        log.info("Received request to get all users");
        return get("");
    }

    public ResponseEntity<Object> deleteUser(long userId) {
        log.info("Received request to delete user with userId={}", userId);
        return delete("/" + userId);
    }
}