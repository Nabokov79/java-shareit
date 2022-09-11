package ru.practicum.shareit.exeption;
public class BadRequestException extends RuntimeException {

    private String massage;

    public BadRequestException(String massage) {
        super(massage);
    }

    public String getMassage() {
        return massage;
    }
}