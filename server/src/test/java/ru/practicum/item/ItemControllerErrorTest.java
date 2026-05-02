package ru.practicum.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.exception.ValidationException;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.servise.ItemServise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemControllerErrorTest {

    @Mock
    private ItemServise itemService;

    @InjectMocks
    private ItemController itemController;

    @Test
    @DisplayName("Контролер - создание вещи, ошибка: не заполнен заголовок с id пользователя")
    void createItem() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                itemController.createItem(new ItemDto(), null)
        );
        assertEquals("Не указан владелец вещи", ex.getMessage());
    }

    @Test
    @DisplayName("Контролер - обновление вещи, ошибка: не заполнен заголовок с id пользователя")
    void updateItem() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                itemController.updateItem(new ItemDto(), null, 1L)
        );
        assertEquals("Не указан владелец вещи", ex.getMessage());
    }

    @Test
    @DisplayName("Контролер - получение вещи по id, ошибка: не заполнен заголовок с id пользователя")
    void getItemById() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                itemController.getItemById(1L, null)
        );
        assertEquals("Не указан пользователь выполнивший запрос", ex.getMessage());
    }

    @Test
    @DisplayName("Контролер - получение вещей пользователя, ошибка: не заполнен заголовок с id пользователя")
    void getAllItemByUserID() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                itemController.getAllItemByUserID(null)
        );
        assertEquals("Не указан владелец вещи", ex.getMessage());
    }

    @Test
    @DisplayName("Контролер - поиск вещи, ошибка: не заполнен заголовок с id пользователя")
    void itemSearch() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                itemController.itemSearch("text", null)
        );
        assertEquals("Не указан пользователь выполнивший запрос", ex.getMessage());
    }

    @Test
    @DisplayName("Контролер - создание комментария, ошибка: не заполнен заголовок с id пользователя")
    void createComment() {
        ValidationException ex = assertThrows(ValidationException.class, () ->
                itemController.createComment(new CommentDto(), null, 1L)
        );
        assertEquals("Не указан пользователь добавляющий комментарий", ex.getMessage());
    }
}
