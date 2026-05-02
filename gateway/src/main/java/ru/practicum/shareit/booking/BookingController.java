package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                                @RequestBody @Valid BookingDto bookingDto) {
        log.info("Gateway: Создание бронирования для пользователя {} вещи {}", userId, bookingDto.getItemId());
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> bookingApproval(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                                  @PathVariable long bookingId,
                                                  @RequestParam boolean approved) {
        log.info("Gateway: Подтверждение бронирования {} пользователем {}", bookingId, userId);
        return bookingClient.bookingApproval(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingById(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                                  @PathVariable long bookingId) {
        log.info("Gateway: Получение бронирования {} пользователем {}", bookingId, userId);
        return bookingClient.findBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findBookingByUser(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        log.info("Gateway: Получение бронирований пользователя {} со статусом {}", userId, state);
        return bookingClient.findBookingByUser(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingByOwner(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                                     @RequestParam(defaultValue = "ALL") String state) {
        log.info("Gateway: Получение бронирований для вещей владельца {} со статусом {}", userId, state);
        return bookingClient.findBookingByOwner(userId, state);
    }
}
