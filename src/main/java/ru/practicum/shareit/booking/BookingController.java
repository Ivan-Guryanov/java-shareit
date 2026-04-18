package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoCreate createBooking(@RequestBody BookingDto bookingDto,
                                          @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Получен запрос на бронирование вещи id {}", bookingDto.getItemId());
        if (userId == null) {
            throw new ValidationException("Не указан создатель бронирования");
        }
        BookingDtoCreate createBooking = bookingService.createBooking(userId, bookingDto);
        log.info("Вещь {} забронирована пользователем {}", createBooking.getItem(), createBooking.getBooker());
        return createBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoCreate bookingApproval(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                            @PathVariable Long bookingId,
                                            @RequestParam Boolean approved) {
        log.info("Получен запрос на подтверждение бронирования вещи id {}", bookingId);
        if (userId == null) {
            throw new ValidationException("Не указан владелец вещи");
        }
        BookingDtoCreate booking = bookingService.bookingApproval(userId, bookingId, approved);
        log.info("Бронирование вещи id {} подтверждено пользователем id {}", booking.getItem().getId(),
                booking.getBooker().getId());
        return booking;
    }

    @GetMapping("/{bookingId}")
    public BookingDtoCreate findBookingById(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Получен запрос от пользователя id {} на получение данных бронирования id {}",userId, bookingId);
        if (userId == null) {
            throw new ValidationException("Не указан владелец вещи");
        }
        BookingDtoCreate booking = bookingService.findBookingById(userId, bookingId);
        log.info("Данные для пользователя id {} о бронирование id {} получены", userId, bookingId);
        return booking;
    }

    @GetMapping
    public Collection<BookingDtoCreate> findBookingByUser(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                          @RequestParam (defaultValue = "ALL") String state) {
        log.info("Получен запрос на получение бронирований пользователя id {} с статусом {}", userId, state);
        if (userId == null) {
            throw new ValidationException("Не указан бронирующий");
        }
        Collection<BookingDtoCreate> bookings = bookingService.findBookingByUser(userId, state);
        log.info("Данные бронирований пользователя id {} с статусом {} получены", userId, state);
        return bookings;
    }

    @GetMapping("/owner")
    public Collection<BookingDtoCreate> findBookingByOwner(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                           @RequestParam (defaultValue = "ALL") String state) {
        log.info("Получен запрос на получение бронирований вещей пользователя id {} с статусом {}", userId, state);
        if (userId == null) {
            throw new ValidationException("Не указан владелец вещи");
        }
        Collection<BookingDtoCreate> bookings = bookingService.findBookingByOwner(userId, state);
        log.info("Данные бронирований вещей пользователя id {} с статусом {} получены", userId, state);
        return bookings;
    }


}
