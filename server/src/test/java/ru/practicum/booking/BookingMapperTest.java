package ru.practicum.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingMapper;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class BookingMapperTest {

    @Test
    @DisplayName("BookingMapper: маппинг сущности в DTO")
    void shouldMapToDto() {
        // given
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .itemId(10L)
                .booker(5L)
                .status(BookingStatus.WAITING)
                .build();

        // when
        BookingDto dto = BookingMapper.mapToDto(booking);

        // then
        assertThat(dto, allOf(
                hasProperty("id", equalTo(1L)),
                hasProperty("start", equalTo(start)),
                hasProperty("end", equalTo(end)),
                hasProperty("itemId", equalTo(10L)),
                hasProperty("booker", equalTo(5L)),
                hasProperty("status", equalTo(BookingStatus.WAITING))
        ));
    }
}
