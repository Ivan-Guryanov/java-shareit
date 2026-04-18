package ru.practicum.shareit.item.servise;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiseImpl implements ItemServise {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;


    @Override
    public ItemDto createItem(Long userId, ItemDto item) {
        item.setOwner(userId);
        Item newItem = ItemDtoMapper.mapToItem(item);

        userService.getUsetById(userId); //проверка существования пользователя

        Item createItem = itemRepository.save(newItem);
        return ItemDtoMapper.mapToDto(createItem);
    }

    @Override
    public ItemDto updateItem(Long id, Long userId, ItemDto item) {
        userService.getUsetById(userId); //проверка существования пользователя
        item.setId(id);
        item.setOwner(userId);

        Item oldItem = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещи с id " + id + "нет в базе"));

        if (item.getName() == null) {
            item.setName(oldItem.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(oldItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(oldItem.getAvailable());
        }
        if (item.getRequest() == null) {
            item.setRequest(oldItem.getRequest());
        }

        Item newItem = ItemDtoMapper.mapToItem(item);
        itemRepository.save(newItem);
        return ItemDtoMapper.mapToDto(newItem);
    }

    @Override
    public ItemDto getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));

        return ItemDtoMapper.mapToDto(item);
    }

    @Override
    public Collection<ItemDto> getAllItemByUserID(Long userId) {
        Collection<ItemDto> userItems = itemRepository.findAll().stream()
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
        Collection<ItemDto> itemSearch = itemRepository.findAll().stream()
                .filter(item -> item.getName().toLowerCase().contains(textSearch))
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .map(ItemDtoMapper::mapToDto)
                .collect(Collectors.toCollection(ArrayList::new));

        return itemSearch;
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        List<Booking> bookers = bookingRepository.findAllByItemId(itemId);

        ArrayList<Booking> bookersApproved = bookers.stream()
                .filter(booking -> booking.getBooker().equals(userId))
                .filter(booking -> booking.getStatus() == BookingStatus.APPROVED)
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .collect(Collectors.toCollection(ArrayList::new));

        if (bookersApproved.isEmpty()) {
            throw new ValidationException("Пользователь не брал вещь");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        Comment comment = CommentMapper.mapToComment(commentDto, user);
        comment.setItem(item);

        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.mapToDto(savedComment);
    }

    @Override
    public ItemWithCommentsDto getItemWithCommentsById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));
        Collection<Comment> comments = commentRepository.findCommentsByItemId(id);
        Collection<CommentDto> commentDto = comments.stream()
                .map(CommentMapper::mapToDto)
                .collect(Collectors.toList());

        return ItemDtoMapper.mapToItemWithCommentsDto(item, commentDto);
    }

}
