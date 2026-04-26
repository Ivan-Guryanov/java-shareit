package ru.practicum.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.item.Item;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;

    private String text;

    private Item item;

    private String authorName;

    private LocalDateTime created;
}