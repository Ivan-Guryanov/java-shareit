package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private User requestorId;
    private List<Item> items;
    private User userId;
}
