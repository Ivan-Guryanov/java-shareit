package ru.practicum.request.dto;

import ru.practicum.request.ItemRequest;

public class ItemRequestMapper {

    public static ItemRequestDto mapToDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestorId(itemRequest.getRequestorId())
                .itemId(itemRequest.getItemId())
                .userId(itemRequest.getUserId())
                .build();
    }

    public static ItemRequest mapToItemRequest(ItemRequestDto itemRequest) {
        return ItemRequest.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestorId(itemRequest.getRequestorId())
                .itemId(itemRequest.getItemId())
                .userId(itemRequest.getUserId())
                .build();
    }
}
