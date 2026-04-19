package ru.practicum.shareit.item.servise;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentsDto;

import java.util.Collection;

public interface ItemServise {
    ItemDto createItem(Long userId, ItemDto item);

    ItemDto updateItem(Long id, Long userId, ItemDto item);

    ItemDto getItemById(Long id);

    Collection<ItemDto> getAllItemByUserID(Long userId);

    Collection<ItemDto> itemSearch(String text);

    CommentDto createComment(Long userId, Long itemId, CommentDto comment);

    ItemWithCommentsDto getItemWithCommentsById(Long id);

}
