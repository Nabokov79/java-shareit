package ru.practicum.shareit.exeption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Map;

@ControllerAdvice
public class ExceptionsHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler
    protected ResponseEntity<Object> handleBadRequest(final BadRequestException ex) {
        logger.error("Bad request error: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}