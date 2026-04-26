package ru.practicum.request.service;

import ru.practicum.request.dto.ItemRequestDto;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long requestorId);
}
