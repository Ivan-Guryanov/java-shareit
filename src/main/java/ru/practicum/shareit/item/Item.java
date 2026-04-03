package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {

    long id;

    @NotBlank(message = "Не указано название товара")
    String name;

    @NotBlank(message = "Не указано описание товара")
    String description;

    @NotNull(message = "Не указан статус вещи")
    Boolean available;

    @NotNull(message = "Владелец должен быть указан")
    Long owner;

    String request;
}