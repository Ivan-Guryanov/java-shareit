package ru.practicum.request.dto;

import lombok.*;
import ru.practicum.item.Item;
import ru.practicum.user.User;

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
