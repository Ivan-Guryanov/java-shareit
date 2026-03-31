package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {

    @NotBlank(message = "ID должен быть указан")
    long id;

    @NotBlank(message = "Название должно быть указано")
    String name;

    String description;

    String available;

    @NotBlank(message = "Владелец должен быть указан")
    long owner;

    String request;
}