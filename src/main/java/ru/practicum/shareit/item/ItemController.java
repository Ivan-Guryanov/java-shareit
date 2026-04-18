package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;
import ru.practicum.shareit.item.servise.ItemServise;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemServise itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto item,
                              @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Получен запрос на добавление вещи");
        if (userId == null) {
            throw new ValidationException("Не указан владелец вещи");
        }
        ItemDto createItem = itemService.createItem(userId, item);
        log.info("Вещь добавлена с id {}", createItem.getId());
        return createItem;
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestBody ItemDto item,
                              @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                              @PathVariable Long id) {
        log.info("Получен запрос на оюновление вещи");
        if (userId == null) {
            throw new ValidationException("Не указан владелец вещи");
        }
        ItemDto updateItem = itemService.updateItem(id, userId, item);
        log.info("Вещь с id {} оюновлена", updateItem.getId());
        return updateItem;
    }

    @GetMapping("/{id}")
    public ItemWithCommentsDto getItemById(@PathVariable  Long id,
                                           @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Получен запрос на получение вещи с id {}", id);
        if (userId == null) {
            throw new ValidationException("Не указан пользователь выполнивший запрос");
        }
        ItemWithCommentsDto getItemById = itemService.getItemWithCommentsById(id);
        log.info("Вещи с id {} успешно получен", getItemById.getId());
        return getItemById;
    }

    @GetMapping
    public Collection<ItemDto> getAllItemByUserID(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Получен запрос на получение всех вещей пользователя id {}", userId);
        if (userId == null) {
            throw new ValidationException("Не указан владелец вещи");
        }
        Collection<ItemDto> userItems = itemService.getAllItemByUserID(userId);
        log.info("Список вещей пользователя id {} успешно получен", userId);
        return userItems;
    }

    @GetMapping("/search")
    public Collection<ItemDto> itemSearch(@RequestParam(name = "text") String text,
                                          @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Выполняется поиск по условию - \"{}\"", text);
        if (userId == null) {
            throw new ValidationException("Не указан пользователь выполнивший запрос");
        }
        Collection<ItemDto> itemSearch = itemService.itemSearch(text);
        log.info("По условию \"{}\" найдено {} вещей", text, itemSearch.size());
        return itemSearch;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto comment,
                                    @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                    @PathVariable Long itemId) {
        log.info("Получен запрос от пользователя id {} на добавления коментария к вещи id {}", userId, itemId);
        if (userId == null) {
            throw new ValidationException("Не указан пользователь добавляющий комментарий");
        }
        CommentDto commentDto = itemService.createComment(userId, itemId, comment);
        log.info("Комментарий к вещи id {} успешно добавлен пользователем id {}", itemId, userId);
        return commentDto;
    }

}
