package ru.practicum.request.service;

import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.RequestDto;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long requestorId);

    Collection<ItemRequestDto> userItemRequest(Long requestorId);

    Collection<ItemRequestDto> allItemRequest(Long userId);

    RequestDto findItemRequestById(Long id);
}
