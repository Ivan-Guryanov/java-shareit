package ru.practicum.request.dto;

import lombok.*;
import ru.practicum.item.Item;
import ru.practicum.user.User;

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
