package ru.practicum.shareit.item.dto;

import lombok.NonNull;

/**
 * TODO Sprint add-controllers.
 */
public class ItemDto {
    @NonNull
    long id;

    @NonNull
    String name;

    String description;

    String available;

    @NonNull
    long owner;

    String request;
}
