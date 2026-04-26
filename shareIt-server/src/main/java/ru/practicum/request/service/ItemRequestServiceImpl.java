package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.ItemRequest;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestMapper;
import ru.practicum.request.storage.ItemRequestRepository;
import ru.practicum.user.User;
import ru.practicum.user.storage.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long requestorId) {
        User user = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        itemRequestDto.setRequestorId(user);
        ItemRequest newItemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto);
        ItemRequest creareItemRequest = itemRequestRepository.save(newItemRequest);
        return ItemRequestMapper.mapToDto(creareItemRequest);
    }

}
