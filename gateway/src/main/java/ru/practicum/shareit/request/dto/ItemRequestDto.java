package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemRequestDto {

    private Long id;
    private String description;
    private LocalDateTime created;
    private User requestorId;
    private Item itemId;
    private User userId;
}
