package ru.practicum.item;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.booking.Booking;
import ru.practicum.booking.BookingStatus;
import ru.practicum.item.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemWithCommentsDto;
import ru.practicum.item.servise.ItemServise;
import ru.practicum.user.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:shareit;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.sql.init.mode=never",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class ItemServiseImplTest {

    private final ItemServise service;
    private final EntityManager em;

    @Test
    @DisplayName("Сервис(интеграционный тест) - создание вещи")
    void createItem() {
        // given
        User owner = makeUser("owner@test.ru", "Owner");
        em.persist(owner);

        ItemDto itemDto = ItemDto.builder()
                .name("Отвертка")
                .description("Крестовая")
                .available(true)
                .build();

        // when
        ItemDto result = service.createItem(owner.getId(), itemDto);

        // then
        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo("Отвертка"));

        Item savedItem = em.find(Item.class, result.getId());
        assertThat(savedItem.getOwner(), equalTo(owner.getId()));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - обновление вещи")
    void updateItem() {
        // given
        User owner = makeUser("owner@test.ru", "Owner");
        em.persist(owner);

        Item oldItem = makeItem("Старое имя", owner.getId());
        oldItem.setDescription("Старое описание");
        oldItem.setAvailable(false);
        em.persist(oldItem);
        em.flush();

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Новое имя");
        updateDto.setDescription("Новое описание");
        updateDto.setAvailable(true);
        updateDto.setRequestId(1L);

        // when
        ItemDto result = service.updateItem(oldItem.getId(), owner.getId(), updateDto);

        // then
        assertThat(result.getName(), equalTo("Новое имя"));
        assertThat(result.getDescription(), equalTo("Новое описание"));
        assertThat(result.getAvailable(), is(true));
        assertThat(result.getRequestId(), equalTo(1L));

        Item updatedInDb = em.find(Item.class, oldItem.getId());
        assertThat(updatedInDb.getName(), equalTo("Новое имя"));
        assertThat(updatedInDb.getDescription(), equalTo("Новое описание"));
        assertThat(updatedInDb.getAvailable(), is(true));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - получение вещи по id")
    void getItemById() {
        // given
        User owner = makeUser("owner@test.ru", "Owner");
        em.persist(owner);

        Item item = makeItem("Молоток", owner.getId());
        em.persist(item);
        em.flush();

        // when
        ItemDto result = service.getItemById(item.getId());

        // then
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(item.getId()));
        assertThat(result.getName(), equalTo("Молоток"));
        assertThat(result.getOwner(), equalTo(owner.getId()));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - получение вещи по несуществующему id")
    void getItemById_whenNotFound() {
        // given
        Long itemId = 9999L;

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(ru.practicum.exception.NotFoundException.class, () -> {
            service.getItemById(itemId);
        });
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - получение всех вещей пользователя")
    void getAllItemByUserID_shouldReturnOnlyUserItems() {
        // given
        User user1 = makeUser("user1@test.ru", "User1");
        User user2 = makeUser("user2@test.ru", "User2");
        em.persist(user1);
        em.persist(user2);


        Item item1 = makeItem("Вещь 1", user1.getId());
        Item item2 = makeItem("Вещь 2", user1.getId());

        Item item3 = makeItem("Вещь 3", user2.getId());

        em.persist(item1);
        em.persist(item2);
        em.persist(item3);
        em.flush();

        // when
        Collection<ItemDto> result = service.getAllItemByUserID(user1.getId());

        // then
        assertThat(result, hasSize(2));
        assertThat(result, hasItem(hasProperty("name", equalTo("Вещь 1"))));
        assertThat(result, hasItem(hasProperty("name", equalTo("Вещь 2"))));
        assertThat(result, not(hasItem(hasProperty("name", equalTo("Вещь 3")))));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - поиск вещи по названию")
    void itemSearch() {
        // given
        User owner = makeUser("owner@test.ru", "Owner");
        em.persist(owner);

        Item item1 = makeItem("Дрель электрическая", owner.getId());
        Item item2 = makeItem("Пила", owner.getId());
        em.persist(item1);
        em.persist(item2);
        em.flush();

        // when
        Collection<ItemDto> result = service.itemSearch("дрель");

        // then
        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getName(), containsStringIgnoringCase("дрель"));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - создание комментария")
    void createComment() {
        // given
        User owner = makeUser("owner@test.ru", "Owner");
        User booker = makeUser("booker@test.ru", "Booker");
        em.persist(owner);
        em.persist(booker);

        Item item = makeItem("Вещь", owner.getId());
        em.persist(item);

        // Завершенное одобренное бронирование (условие для комментария)
        Booking booking = Booking.builder()
                .itemId(item.getId())
                .booker(booker.getId())
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build();
        em.persist(booking);
        em.flush();

        CommentDto commentDto = CommentDto.builder().text("Текст комментария").build();

        // when
        CommentDto result = service.createComment(booker.getId(), item.getId(), commentDto);

        // then
        assertThat(result.getId(), notNullValue());
        assertThat(result.getAuthorName(), equalTo("Booker"));
        assertThat(result.getText(), equalTo("Текст комментария"));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - получение вещи по id с комментариями")
    void getItemWithCommentsById() {
        // given
        User owner = makeUser("owner@test.ru", "Owner");
        User author = makeUser("author@test.ru", "Author");
        em.persist(owner);
        em.persist(author);

        Item item = makeItem("Вещь", owner.getId());
        em.persist(item);

        Comment comment = new Comment();
        comment.setText("Крутая вещь");
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        em.persist(comment);
        em.flush();

        // when
        ItemWithCommentsDto result = service.getItemWithCommentsById(item.getId());

        // then
        assertThat(result.getName(), equalTo("Вещь"));
        assertThat(result.getComments(), hasSize(1));
        assertThat(result.getComments().iterator().next().getText(), equalTo("Крутая вещь"));
    }

    private User makeUser(String email, String name) {
        return User.builder()
                .name(name)
                .email(email)
                .build();
    }

    private Item makeItem(String name, Long ownerId) {
        return Item.builder()
                .name(name)
                .description("Описание")
                .available(true)
                .owner(ownerId)
                .build();
    }
}
