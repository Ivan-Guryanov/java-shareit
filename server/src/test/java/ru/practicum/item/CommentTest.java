package ru.practicum.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {

    @Test
    @DisplayName("Модель - проверка Equals и HashCode")
    void testEqualsAndHashCode() {
        Comment comment1 = Comment.builder().id(1L).text("Great item!").build();
        Comment comment2 = Comment.builder().id(1L).text("Great item!").build();
        Comment comment3 = Comment.builder().id(2L).text("Bad item!").build();

        // 1. Проверка на идентичность (this == o)
        assertTrue(comment1.equals(comment1));

        // 2. Проверка на null (o == null)
        assertFalse(comment1.equals(null));

        // 3. Проверка на другой класс
        assertFalse(comment1.equals(new Object()));

        // 4. Сравнение двух разных объектов с одинаковым ID (равенство по id)
        assertEquals(comment1, comment2);

        // 5. Сравнение объектов с разными ID (неравенство)
        assertNotEquals(comment1, comment3);

        // 6. Проверка hashCode (зависит от id)
        assertEquals(comment1.hashCode(), comment2.hashCode());
        assertNotEquals(comment1.hashCode(), comment3.hashCode());
    }

    @Test
    @DisplayName("Модель - сравнение объектов без ID")
    void testEqualsWithNullId() {
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();

        assertFalse(comment1.equals(comment2));

        assertEquals(comment1.hashCode(), comment2.hashCode());
    }


}
