package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.service.ItemRequestService;

import java.util.Collection;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                             @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Получен запрос на добавление запроса на вещь от пользователя id {}", requestorId);
        ItemRequestDto createItemRequest = itemRequestService.createItemRequest(itemRequestDto, requestorId);
        log.info("Запрос на добалвение запроса на вещь от пользователя id {} " +
                "добавлен с id {}", createItemRequest.getRequestorId(), createItemRequest.getId());
        return createItemRequest;
    }

    @GetMapping
    public Collection<ItemRequestDto> userItemRequest(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Получен запрос на получение всех запросов вещей пользователя id {}", requestorId);
        Collection<ItemRequestDto> userItemRequest = itemRequestService.userItemRequest(requestorId);
        log.info("Получены все запросы вещей пользователя id {}", requestorId);
        return userItemRequest;
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> allItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение всех запросов вещей от пользователя id {}", userId);
        Collection<ItemRequestDto> allItemRequest = itemRequestService.allItemRequest(userId);
        log.info("Получены все запросы вещей");
        return allItemRequest;
    }

    @GetMapping("/{requestId}")
    public RequestDto findItemRequestById(@PathVariable Long requestId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос получение запроса вещи id {}", requestId);
        RequestDto findItemRequestById = itemRequestService.findItemRequestById(requestId);
        log.info("Получен запрос вещи id {}", findItemRequestById.getId());
        return findItemRequestById;
    }
}
