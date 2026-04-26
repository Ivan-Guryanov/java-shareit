package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.item.Item;
import ru.practicum.item.storage.ItemRepository;
import ru.practicum.request.ItemRequest;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.ItemRequestMapper;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.storage.ItemRequestRepository;
import ru.practicum.user.User;
import ru.practicum.user.storage.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long requestorId) {
        User user = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        itemRequestDto.setRequestorId(user);
        ItemRequest newItemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto);
        ItemRequest creareItemRequest = itemRequestRepository.save(newItemRequest);
        return ItemRequestMapper.mapToDto(creareItemRequest);
    }

    @Transactional
    public Collection<ItemRequestDto> userItemRequest(Long requestorId) {
        Collection<ItemRequest> userItemRequest = itemRequestRepository.findAllByRequestorId_Id(requestorId);
        Collection<ItemRequestDto> list = userItemRequest.stream()
                .map(ItemRequestMapper::mapToDto)
                .collect(Collectors.toSet());

        return list;
    }

    @Transactional
    public Collection<ItemRequestDto> allItemRequest(Long userId) {
        Collection<ItemRequest> allItemRequest = itemRequestRepository.findAll();
        Collection<ItemRequestDto> list = allItemRequest.stream()
                .map(ItemRequestMapper::mapToDto)
                .collect(Collectors.toSet());
        return list;
    }

    @Transactional
    public RequestDto findItemRequestById(Long id) {

        ItemRequest request = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + id + " не найден"));
        Item item = itemRepository.findByRequestId(id);
        System.out.println(item);
        request.setItemId(item);
        return ItemRequestMapper.mapToRequestDto(request);
    }
}
