package ru.practicum.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingStatus;
import ru.practicum.booking.dto.BookingDto;
import ru.practicum.booking.dto.BookingDtoCreate;
import ru.practicum.booking.dto.BookingMapper;
import ru.practicum.booking.storage.BookingRepository;
import ru.practicum.exception.ValidationException;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.servise.ItemServise;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemServise itemServise;

    @Transactional
    public BookingDtoCreate createBooking(Long userId, BookingDto bookingDto) {
        ItemDto item = itemServise.getItemById(bookingDto.getItemId());  //проверка наличия вещи
        userService.getUsetById(userId); //проверка существования пользователя

        LocalDateTime now = LocalDateTime.now();
        System.out.println(bookingDto);
        System.out.println(bookingDto.getStart());

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

    @Transactional
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

    @Transactional
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
    @Transactional
    public Collection<BookingDtoCreate> findBookingByUser(Long userId, String state) {
        userService.getUsetById(userId); //проверка существования пользователя
        List<Booking> bookers = bookingRepository.findAllByBooker(userId);
        return filterListBookings(bookers, state);
    }

    @Transactional
    public Collection<BookingDtoCreate> findBookingByOwner(Long userId, String state) {
        userService.getUsetById(userId); //проверка существования пользователя
        List<Booking> bookers = bookingRepository.findAllByOwnerId(userId);
        return filterListBookings(bookers, state);
    }

    @Transactional
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