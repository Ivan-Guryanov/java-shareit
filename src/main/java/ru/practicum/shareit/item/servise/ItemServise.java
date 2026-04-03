package ru.practicum.shareit.item.servise;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemServise {
    ItemDto createItem(Long userId, Item item);

    ItemDto updateItem(Long id, Long userId, Item item);

    ItemDto getItemById(Long id);

    Collection<ItemDto> getAllItemByUserID(Long userId);

    Collection<ItemDto> itemSearch(String text);
}
