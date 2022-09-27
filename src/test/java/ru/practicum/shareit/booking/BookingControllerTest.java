package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exeption.BadRequestException;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;
    @MockBean
    BookingService bookingService;


    @Test
    void createBooking() throws Exception {
        BookingRequestDto booking = new BookingRequestDto(1L, 1L, LocalDateTime.now().plusHours(1),
                                                                            LocalDateTime.now().plusDays(1));
        String body = mapper.writeValueAsString(booking);
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .content(body))
                        .andExpect(status().isOk());
    }

    @Test
    void confirmBooking() throws Exception {
        mockMvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                        .andExpect(status().isOk());
    }

    @Test
    void confirmBooking2() throws Exception {
        when(bookingService.confirmBooking(1L, false,1L)).thenThrow(BadRequestException.class);
        mockMvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "false"))
                        .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingById() throws Exception {
        mockMvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                        .andExpect(status().isOk());
    }

    @Test
    void getAllBookingsByBookerId() throws Exception {
        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "1")
                        .param("size", "1")
                        .param("state", "FUTURE"))
                        .andExpect(status().isOk());
    }

    @Test
    void getAllBookingsByBookerId_FromNoPositive() throws Exception {
        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "-1")
                        .param("size", "1")
                        .param("state", "FUTURE"))
                        .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBookingsByBookerId_SizeNoPositive() throws Exception {
        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "1")
                        .param("size", "-1")
                        .param("state", "FUTURE"))
                        .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBookingsOwnerItem() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "1")
                        .param("size", "1")
                        .param("state", "FUTURE"))
                        .andExpect(status().isOk());
    }
}