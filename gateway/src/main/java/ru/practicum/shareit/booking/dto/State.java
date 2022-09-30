package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;
    public static Optional<State> from(String stringState) {
        for (ru.practicum.shareit.booking.dto.State state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return java.util.Optional.of(state);
            }
        }
        return java.util.Optional.empty();
    }
}