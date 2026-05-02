package ru.practicum.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class ItemRequestTest {

    @Test
    @DisplayName("Модель - проверка Equals и HashCode")
    void testEqualsAndHashCode() {
        ItemRequest request1 = ItemRequest.builder().id(1L).description("Нужен молоток").build();
        ItemRequest request2 = ItemRequest.builder().id(1L).description("Нужен молоток").build();
        ItemRequest request3 = ItemRequest.builder().id(2L).description("Нужна дрель").build();

        // 1. Проверка на идентичность (this == o)
        assertTrue(request1.equals(request1));

        // 2. Проверка на null (o == null)
        assertFalse(request1.equals(null));

        // 3. Проверка на другой класс (thisEffectiveClass != oEffectiveClass)
        assertFalse(request1.equals(new Object()));

        // 4. Сравнение двух разных объектов с одинаковым ID (равенство по id)
        assertEquals(request1, request2);

        // 5. Сравнение объектов с разными ID (неравенство)
        assertNotEquals(request1, request3);

        // 6. Проверка hashCode (зависит от id)
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    @DisplayName("Модель - сравнение объектов без ID")
    void testEqualsWithNullId() {
        // Проверка случая, когда getId() == null ( getId() != null && ... в equals)
        ItemRequest request1 = new ItemRequest();
        ItemRequest request2 = new ItemRequest();

        assertFalse(request1.equals(request2));

        assertEquals(request1.hashCode(), request2.hashCode());
    }
}
