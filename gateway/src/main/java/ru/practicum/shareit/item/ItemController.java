package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Gateway: Создание вещи для пользователя id {}", userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Gateway: Обновление вещи id {} пользователем id {}", itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId) {
        log.info("Gateway: Получение вещи id {} пользователем id {}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemByUserID(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Gateway: Получение всех вещей пользователя id {}", userId);
        return itemClient.getAllItemByUserID(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> itemSearch(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam @NotBlank(message = "Текст поиска не может быть пустым")String text) {
        log.info("Gateway: Поиск вещей по запросу: {}", text);
        return itemClient.itemSearch(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable long itemId,
                                                @RequestBody CommentDto commentDto) {
        log.info("Gateway: Добавление комментария к вещи id {} пользователем id {}", itemId, userId);
        return itemClient.createComment(userId, itemId, commentDto);
    }
}