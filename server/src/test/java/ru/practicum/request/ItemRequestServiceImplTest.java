package ru.practicum.request;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.item.Item;
import ru.practicum.request.dto.ItemRequestDto;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.service.ItemRequestService;
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
class ItemRequestServiceImplTest {

    private final ItemRequestService service;
    private final EntityManager em;

    @Test
    @DisplayName("Сервис(интеграционный тест) - создание запроса вещи")
    void createItemRequest() {
        // given
        User requestor = makeUser("requestor@mail.ru", "Ivan");
        em.persist(requestor);
        em.flush();

        ItemRequestDto inputDto = ItemRequestDto.builder()
                .description("Нужна лестница")
                .created(LocalDateTime.now())
                .build();

        // when
        ItemRequestDto result = service.createItemRequest(inputDto, requestor.getId());

        // then
        assertThat(result.getId(), notNullValue());
        assertThat(result.getDescription(), equalTo("Нужна лестница"));
        assertThat(result.getRequestorId().getName(), equalTo("Ivan"));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - получение запросов вещей пользователя id")
    void userItemRequest() {
        // given
        User user = makeUser("user@mail.ru", "User");
        em.persist(user);

        ItemRequest request1 = ItemRequest.builder()
                .description("Запрос 1").requestorId(user).created(LocalDateTime.now()).build();
        ItemRequest request2 = ItemRequest.builder()
                .description("Запрос 2").requestorId(user).created(LocalDateTime.now()).build();
        em.persist(request1);
        em.persist(request2);
        em.flush();

        // when
        Collection<ItemRequestDto> result = service.userItemRequest(user.getId());

        // then
        assertThat(result, hasSize(2));
        assertThat(result, hasItem(hasProperty("description", equalTo("Запрос 1"))));
        assertThat(result, hasItem(hasProperty("description", equalTo("Запрос 2"))));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - получение списка запросов созданных другими пользователями")
    void allItemRequest() {
        // given
        User user1 = makeUser("user1@mail.ru", "User1");
        User user2 = makeUser("user2@mail.ru", "User2");
        em.persist(user1);
        em.persist(user2);

        ItemRequest request1 = ItemRequest.builder()
                .description("Нужна вещь 1")
                .requestorId(user1)
                .created(LocalDateTime.now())
                .build();

        ItemRequest request2 = ItemRequest.builder()
                .description("Нужна вещь 2")
                .requestorId(user2)
                .created(LocalDateTime.now())
                .build();

        em.persist(request1);
        em.persist(request2);
        em.flush();

        // when
        Collection<ItemRequestDto> result = service.allItemRequest(user1.getId());

        // then
        assertThat(result, hasSize(greaterThanOrEqualTo(2)));

        assertThat(result, hasItems(
                allOf(
                        hasProperty("description", equalTo("Нужна вещь 1")),
                        hasProperty("requestorId", hasProperty("name", equalTo("User1")))
                ),
                allOf(
                        hasProperty("description", equalTo("Нужна вещь 2")),
                        hasProperty("requestorId", hasProperty("name", equalTo("User2")))
                )
        ));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - получение запроса вещи по id")
    void findItemRequestById() {
        // given
        User requestor = makeUser("req@mail.ru", "Ivan");
        User owner = makeUser("owner@mail.ru", "Petr");
        em.persist(requestor);
        em.persist(owner);

        ItemRequest request = ItemRequest.builder()
                .description("Нужна бензопила")
                .requestorId(requestor)
                .created(LocalDateTime.now())
                .build();
        em.persist(request);

        Item item = Item.builder()
                .name("Бензопила")
                .description("Мощная")
                .available(true)
                .owner(owner.getId())
                .requestId(request.getId())
                .build();
        em.persist(item);
        em.flush();

        // when
        RequestDto result = service.findItemRequestById(request.getId());

        // then
        assertThat(result.getId(), equalTo(request.getId()));
        assertThat(result.getDescription(), equalTo("Нужна бензопила"));
        assertThat(result.getItems(), hasSize(1));
        assertThat(result.getItems().get(0).getName(), equalTo("Бензопила"));
    }

    private User makeUser(String email, String name) {
        return User.builder().name(name).email(email).build();
    }
}
