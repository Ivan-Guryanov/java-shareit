package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDtoCreate {

    private Long id;

    @NotNull(message = "Дата начала должна быть заполнена")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания должна быть заполнена")
    private LocalDateTime end;

    private ItemDto item;

    private UserDto booker;

    private BookingDto status;
}
