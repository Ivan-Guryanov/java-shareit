package ru.practicum.shareit.item;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {


    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long owner;

    private Long requestId;


}