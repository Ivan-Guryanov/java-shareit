package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.service.ItemRequestService;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest (@RequestBody ItemRequestDto itemRequestDto,
                                             @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Получен запрос на добавление запроса на вещь от пользователя id {}", requestorId);
        ItemRequestDto createItemRequest = itemRequestService.createItemRequest(itemRequestDto, requestorId);
        log.info("Запрос на добалвение запроса на вещь от пользователя id {} " +
                "добавлен с id {}", createItemRequest.getRequestorId(), createItemRequest.getId());
        return createItemRequest;
    }
}
