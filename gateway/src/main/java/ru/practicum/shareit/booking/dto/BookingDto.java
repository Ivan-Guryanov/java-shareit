package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long id;

    @NotNull(message = "Дата начала должна быть заполнена")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания должна быть заполнена")
    private LocalDateTime end;

    @NotNull(message = "Должен быть указан пользователь")
    private Long itemId;

    private Long booker;

    private ru.practicum.shareit.booking.BookingStatus status;
}
