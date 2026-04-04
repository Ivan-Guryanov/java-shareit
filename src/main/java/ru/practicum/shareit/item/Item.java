package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {

    private long id;

    @NotBlank(message = "Не указано название товара")
    private String name;

    @NotBlank(message = "Не указано описание товара")
    private String description;

    @NotNull(message = "Не указан статус вещи")
    private Boolean available;

    @NotNull(message = "Владелец должен быть указан")
    private Long owner;

    private String request;
}