package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.Item;

import java.util.Collection;

public interface ItemStorage {
    Collection<Item> findAllItem();

    Item createItem(Item item);

    Item updateItem(Item newItem);

    void deleteItem(Long id);

    Item getItemById(Long id);
}
