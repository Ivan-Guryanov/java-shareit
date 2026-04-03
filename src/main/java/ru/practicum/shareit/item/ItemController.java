package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.servise.ItemServise;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemServise itemServise;

    @PostMapping
    public ItemDto createItem(@RequestBody Item item,
                              @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Получен запрос на добавление вещи");
        if (userId == null) {
            throw new ValidationException("Не указан владелец вещи");
        }
        ItemDto createItem = itemServise.createItem(userId, item);
        log.info("Вещь добавлена с id {}", createItem.getId());
        return createItem;
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestBody Item item,
                              @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                              @PathVariable Long id) {
        log.info("Получен запрос на оюновление вещи");
        if (userId == null) {
            throw new ValidationException("Не указан владелец вещи");
        }
        ItemDto updateItem = itemServise.updateItem(id, userId, item);
        log.info("Вещь с id {} оюновлена", updateItem.getId());
        return updateItem;
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable  Long id,
                               @RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Получен запрос на получение вещи с id {}", id);
        if (userId == null) {
            throw new ValidationException("Не указан пользователь выполнивший запрос");
        }
        ItemDto getItemById = itemServise.getItemById(id);
        log.info("Вещи с id {} успешно получен", getItemById.getId());
        return getItemById;
    }

    @GetMapping
    public Collection<ItemDto> getAllItemByUserID(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Получен запрос на получение всех вещей пользователя id {}", userId);
        if (userId == null) {
            throw new ValidationException("Не указан владелец вещи");
        }
        Collection<ItemDto> userItems = itemServise.getAllItemByUserID(userId);
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
        Collection<ItemDto> itemSearch = itemServise.itemSearch(text);
        log.info("По условию \"{}\" найдено {} вещей", text, itemSearch.size());
        return itemSearch;
    }
}
