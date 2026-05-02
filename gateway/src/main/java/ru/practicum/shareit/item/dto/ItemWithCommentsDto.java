package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemWithCommentsDto {
    private Long id;

    @NotBlank(message = "Не введено название")
    private String name;

    @NotBlank(message = "Не введено название")
    private String description;

    private Boolean available;
    private Long lastBooking;
    private Long nextBooking;
    private Collection<ru.practicum.shareit.item.dto.CommentDto> comments;
}