package ru.practicum.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.item.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Item findByRequestId(Long id);
}
