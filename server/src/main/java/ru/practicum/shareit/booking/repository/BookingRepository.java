package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "select b from Booking b " +
                   "where b.booker.id = ?1")
    List<Booking> findBookingByBookerId(long bookerId, Pageable pageable);

    @Query(value = "select b from Booking b " +
                   "left join Item i on i.id = b.item.id " +
                   "where i.owner.id = ?1")
    List<Booking> findBookingByOwnerId(long ownerId, Pageable pageable);

    List<Booking> findBookingByItemId(long itemId);

    List<Booking> findBookingByItemIdAndBookerId(long itemId, long bookerId);
}