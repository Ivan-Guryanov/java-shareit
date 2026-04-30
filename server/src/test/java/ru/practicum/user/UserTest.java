package ru.practicum.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    @DisplayName("Модель - проверка Equals и HashCode")
    void testEqualsAndHashCode() {
        User user1 = User.builder().id(1L).name("Ivan").email("ivan@mail.ru").build();
        User user2 = User.builder().id(1L).name("Ivan").email("ivan@mail.ru").build();
        User user3 = User.builder().id(2L).name("Petr").email("petr@mail.ru").build();

        // 1. Проверка на идентичность (this == o)
        assertTrue(Objects.equals(user1, user1));

        // 2. Проверка на null (o == null)
        assertFalse(user1.equals(null));

        // 3. Проверка на другой класс (thisEffectiveClass != oEffectiveClass)
        assertFalse(user1.equals(new Object()));

        // 4. Сравнение двух разных объектов с одинаковым ID (равенство)
        assertTrue(user1.equals(user2));

        // 5. Сравнение объектов с разными ID
        assertFalse(user1.equals(user3));

        // 6. Проверка hashCode (должен быть одинаковым для одного класса)
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testEqualsWithNullId() {
        User user1 = new User();
        User user2 = new User();

        assertFalse(user1.equals(user2));
    }
}
