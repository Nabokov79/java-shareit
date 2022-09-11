package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        return new BookingResponseDto(booking.getId(),
                                      booking.getStart(),
                                      booking.getEnd(),
                                      booking.getStatus().toString(),
                                      booking.getBooker(),
                                      booking.getItem());
    }

    public static Booking toBooking(BookingRequestDto bookingDto) {
        return new Booking(bookingDto.getId(),
                           bookingDto.getStart(),
                           bookingDto.getEnd(),
                           new Item(),
                           new User(),
                           Status.WAITING);
    }
}
