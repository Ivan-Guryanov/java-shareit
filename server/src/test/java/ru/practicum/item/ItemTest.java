package ru.practicum.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    @Test
    @DisplayName("Модель - проверка Equals и HashCode")
    void testEqualsAndHashCode() {
        Item item1 = Item.builder().id(1L).name("Дрель").description("Мощная").build();
        Item item2 = Item.builder().id(1L).name("Дрель").description("Мощная").build();
        Item item3 = Item.builder().id(2L).name("Пила").description("Острая").build();

        // 1. Проверка на идентичность (this == o)
        assertTrue(item1.equals(item1));

        // 2. Проверка на null (o == null)
        assertFalse(item1.equals(null));

        // 3. Проверка на другой класс
        assertFalse(item1.equals(new Object()));

        // 4. Сравнение двух разных объектов с одинаковым ID (равенство)
        assertEquals(item1, item2);

        // 5. Сравнение объектов с разными ID (неравенство)
        assertNotEquals(item1, item3);

        // 6. Проверка hashCode (зависит от id)
        assertEquals(item1.hashCode(), item2.hashCode());
        assertNotEquals(item1.hashCode(), item3.hashCode());
    }

    @Test
    @DisplayName("Модель - сравнение объектов без ID")
    void testEqualsWithNullId() {
        Item item1 = new Item();
        Item item2 = new Item();

        assertFalse(item1.equals(item2));

        assertEquals(item1.hashCode(), item2.hashCode());
    }

}
