package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class ItemStorageMem implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    long count = 0;

    public Collection<Item> findAllItem() {
        return new ArrayList<>(items.values());
    }

    public Item createItem(Item item) {
        count++;
        item.setId(count);
        items.put(count, item);
        return item;
    }

    public Item updateItem(Item newItem) {
        Item item = getItemById(newItem.getId());
        if (newItem.getName() != null) {
            item.setName(item.getName());
        }

        if (newItem.getDescription() != null) {
            item.setDescription(item.getDescription());
        }

        if (newItem.getAvailable() != null) {
            item.setAvailable(item.getAvailable());
        }
        items.put(newItem.getId(), item);
        return newItem;
    }

    public void deleteItem(Long id) {
        items.remove(id);
    }

    public Item getItemById(Long id) {
        if (items.get(id) == null) {
            throw new NotFoundException("Такого юзера нет");
        }
        return items.get(id);
    }
}
