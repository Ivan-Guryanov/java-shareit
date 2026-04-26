package ru.practicum.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.ItemRequest;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Collection<ItemRequest> findAllByRequestorId_Id(Long requestorId);

}
