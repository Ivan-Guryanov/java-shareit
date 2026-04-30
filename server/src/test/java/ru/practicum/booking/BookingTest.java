package ru.practicum.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingTest {

    @Test
    @DisplayName("Модель - проверка Equals и HashCode")
    void testEqualsAndHashCode() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        Booking booking1 = Booking.builder().id(1L).start(start).end(end).build();
        Booking booking2 = Booking.builder().id(1L).start(start).end(end).build();
        Booking booking3 = Booking.builder().id(2L).start(start).end(end).build();

        // 1. Проверка на идентичность (this == o)
        assertTrue(booking1.equals(booking1));

        // 2. Проверка на null (o == null)
        assertFalse(booking1.equals(null));

        // 3. Проверка на другой класс
        assertFalse(booking1.equals(new Object()));

        // 4. Сравнение двух разных объектов с одинаковым ID (равенство)
        assertEquals(booking1, booking2);

        // 5. Сравнение объектов с разными ID (неравенство)
        assertNotEquals(booking1, booking3);

        // 6. Проверка hashCode (зависит от id)
        assertEquals(booking1.hashCode(), booking2.hashCode());
        assertNotEquals(booking1.hashCode(), booking3.hashCode());
    }

    @Test
    @DisplayName("Модель - сравнение объектов без ID")
    void testEqualsWithNullId() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();

        assertFalse(booking1.equals(booking2));

        assertEquals(booking1.hashCode(), booking2.hashCode());
    }

}
