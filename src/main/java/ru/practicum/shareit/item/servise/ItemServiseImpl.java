package ru.practicum.shareit.item.servise;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiseImpl implements ItemServise {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final Validator validator;

    @Override
    public ItemDto createItem(Long userId, ItemDto item) {
        item.setOwner(userId);
        Item newItem = ItemDtoMapper.mapToItem(item);
        var violations = validator.validate(newItem);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations.iterator().next().getMessage());
        }
        userStorage.getUsetById(userId); //проверка существования пользователя

        Item createItem = itemStorage.createItem(newItem);
        return ItemDtoMapper.mapToDto(createItem);
    }

    @Override
    public ItemDto updateItem(Long id, Long userId, ItemDto item) {
        item.setId(id);
        item.setOwner(userId);

        Item newItem = ItemDtoMapper.mapToItem(item);

        userStorage.getUsetById(userId); //проверка существования пользователя

        Item updateItem = itemStorage.updateItem(newItem);
        return ItemDtoMapper.mapToDto(updateItem);
    }

    @Override
    public ItemDto getItemById(Long id) {
        return ItemDtoMapper.mapToDto(itemStorage.getItemById(id));
    }

    @Override
    public Collection<ItemDto> getAllItemByUserID(Long userId) {
        Collection<ItemDto> userItems = itemStorage.findAllItem().stream()
                .filter(item -> item.getOwner().equals(userId))
                .map(ItemDtoMapper::mapToDto)
                .collect(Collectors.toCollection(ArrayList::new));
        return userItems;
    }

    @Override
    public Collection<ItemDto> itemSearch(String text) {
        String textSearch = text.toLowerCase();

        if (text.equals("")) {
            return new ArrayList<>();
        }
        Collection<ItemDto> itemSearch = itemStorage.findAllItem().stream()
                .filter(item -> item.getName().toLowerCase().contains(textSearch))
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .map(ItemDtoMapper::mapToDto)
                .collect(Collectors.toCollection(ArrayList::new));

        return itemSearch;
    }

}
