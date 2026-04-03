package ru.practicum.shareit.item.dto;

import lombok.*;

/**
 * TODO Sprint add-controllers.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    long id;
    String name;
    String description;
    Boolean available;
    long owner;
    String request;
}
