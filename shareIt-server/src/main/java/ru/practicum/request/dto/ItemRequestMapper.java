package ru.practicum.request.dto;

import ru.practicum.request.ItemRequest;

import java.util.List;

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

    public static RequestDto mapToRequestDto(ItemRequest itemRequest) {
        return RequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestorId(itemRequest.getRequestorId())
                .items(List.of(itemRequest.getItemId()))
                .userId(itemRequest.getUserId())
                .build();
    }

}
