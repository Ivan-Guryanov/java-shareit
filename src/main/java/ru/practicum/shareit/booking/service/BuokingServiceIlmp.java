package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.servise.ItemServise;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuokingServiceIlmp implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemServise itemServise;

    @Override
    public BookingDtoCreate createBooking(Long userId, BookingDto bookingDto) {
        ItemDto item = itemServise.getItemById(bookingDto.getItemId());  //проверка наличия вещи
        userService.getUsetById(userId); //проверка существования пользователя

        LocalDateTime now = LocalDateTime.now();

        if (bookingDto.getStart() == null) {
            throw new ValidationException("Не указана дата начала бронирования");
        }
        if (bookingDto.getEnd() == null) {
            throw new ValidationException("Не указана дата окончания бронирования");
        }
        if (bookingDto.getStart().isBefore(now.minusSeconds(10))) {
            throw new ValidationException("Дата начала бронирования не может быть в прошлом");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Дата окончания не может быть раньше даты начала");
        }
        if (bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new ValidationException("Даты начала и окончания не могут совпадать");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с id " + item.getId() + " не доступна для бронирования");
        }

        bookingDto.setBooker(userId);
        bookingDto.setStatus(BookingStatus.WAITING);

        Booking createBooking = bookingRepository.save(BookingMapper.mapToBooking(bookingDto));

        return updateDtoCreate(createBooking);
    }

    @Override
    public BookingDtoCreate bookingApproval(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).get();
        BookingDtoCreate bookingDtoCreate = updateDtoCreate(booking);
        if (bookingDtoCreate.getItem().getOwner() != userId) {
            throw new ValidationException("Пользователь не является владельцем вещи");
        }
        if (approved) {
            bookingDtoCreate.setStatus(BookingStatus.APPROVED);
            bookingRepository.save(BookingMapper.mapToBookingCreate(bookingDtoCreate));
            ItemDto item = bookingDtoCreate.getItem();
            itemServise.updateItem(item.getId(), item.getOwner(), item);
        } else {
            bookingDtoCreate.setStatus(BookingStatus.REJECTED);
            bookingRepository.save(BookingMapper.mapToBookingCreate(bookingDtoCreate));
        }
        return bookingDtoCreate;
    }

    @Override
    public BookingDtoCreate findBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).get();
        BookingDtoCreate bookingDtoCreate = updateDtoCreate(booking);
        if (!Objects.equals(bookingDtoCreate.getItem().getOwner(), userId) &&
                !Objects.equals(bookingDtoCreate.getBooker().getId(), userId)) {
            throw new ValidationException("Пользователь не является владельцем вещи или бронирующим");
        }
        return bookingDtoCreate;
    }

    // Тут логика должна быть сложней, но ТЗ этого не требует (ввести в BookingStatus - Передана, Не взята в срок, Не возвращена в срок)
    @Override
    public Collection<BookingDtoCreate> findBookingByUser(Long userId, String state) {
        userService.getUsetById(userId); //проверка существования пользователя
        List<Booking> bookers = bookingRepository.findAllByBooker(userId);
        return filterListBookings(bookers, state);
    }

    @Override
    public Collection<BookingDtoCreate> findBookingByOwner(Long userId, String state) {
        userService.getUsetById(userId); //проверка существования пользователя
        List<Booking> bookers = bookingRepository.findAllByOwnerId(userId);
        return filterListBookings(bookers, state);
    }

    private BookingDtoCreate updateDtoCreate(Booking booking) {
        ItemDto item = itemServise.getItemById(booking.getItemId());
        UserDto user = userService.getUsetById(booking.getBooker());
        BookingDtoCreate bookingDtoCreate = BookingMapper.mapToDtoCreate(booking);
        bookingDtoCreate.setItem(item);
        bookingDtoCreate.setBooker(user);
        return bookingDtoCreate;
    }

    private Collection<BookingDtoCreate> filterListBookings(List<Booking> bookers, String state) {
        switch (state) {
            case "ALL":
                return bookers.stream()
                        .map(this::updateDtoCreate)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookers.stream()
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .filter(booking -> booking.getEnd().isAfter(LocalDateTime.now()))
                        .map(this::updateDtoCreate)
                        .collect(Collectors.toList());
            case "PAST":
                return bookers.stream()
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .map(this::updateDtoCreate)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookers.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .map(this::updateDtoCreate)
                        .collect(Collectors.toList());
            case "WAITING":
                return bookers.stream()
                        .filter(booking -> booking.getStatus() == BookingStatus.WAITING)
                        .map(this::updateDtoCreate)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookers.stream()
                        .filter(booking -> booking.getStatus() == BookingStatus.REJECTED)
                        .map(this::updateDtoCreate)
                        .collect(Collectors.toList());
            default:
                throw new ValidationException("Нет информации для фильтрации списка");
        }
    }
}
