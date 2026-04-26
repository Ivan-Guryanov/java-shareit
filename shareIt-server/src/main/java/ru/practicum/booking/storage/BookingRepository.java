package ru.practicum.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.booking.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker(Long bookerId);

    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN items i ON b.item_id = i.id " +
            "WHERE i.owner_id = ?1 " +
            "ORDER BY b.start DESC", nativeQuery = true)
    List<Booking> findAllByOwnerId(Long ownerId);

    List<Booking> findAllByItemId(Long bookerId);
}
