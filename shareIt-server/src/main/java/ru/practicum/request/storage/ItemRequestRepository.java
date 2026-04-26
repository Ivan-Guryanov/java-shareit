package ru.practicum.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
}
