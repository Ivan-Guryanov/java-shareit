package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Gateway: Создание запроса на вещь пользователем {}", userId);
        return requestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> userItemRequest(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Gateway: Получение собственных запросов пользователя {}", userId);
        return requestClient.userItemRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> allItemRequest(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Gateway: Получение всех запросов (from={}, size={}) пользователем {}", from, size, userId);
        return requestClient.allItemRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @PathVariable @Positive long requestId) {
        log.info("Gateway: Получение запроса id {} пользователем {}", requestId, userId);
        return requestClient.findItemRequestById(userId, requestId);
    }
}
