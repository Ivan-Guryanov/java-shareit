package ru.practicum.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserDtoMapper;
import ru.practicum.user.service.UserService;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/shareit",
        "spring.datasource.username=shareit",
        "spring.datasource.password=shareit",
})
class UserServiceImplTest {

    private final EntityManager em;
    private final UserService service;

    @Test
    @DisplayName("Сервис(интеграционный тест) - создание пользователя.")
    void createUser() {
        // given
        UserDto userDto = makeUserDto("ivan@yandex.ru", "Ivan");

        // when
        service.createUser(userDto);

        // then
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - получение списка всех пользователей.")
    void findAllUser() {
        // given
        List<UserDto> sourceUsers = List.of(
                makeUserDto("ivan@email.ru", "Ivan"),
                makeUserDto("petr@email.ru", "Petr"),
                makeUserDto("vasya@email.ru", "Vasya")
        );

        for (UserDto dto : sourceUsers) {
            User entity = UserDtoMapper.mapToUser(dto);
            em.persist(entity);
        }
        em.flush();

        // when
        Collection<UserDto> targetUsers = service.findAllUser();

        // then
        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - обновление данных пользователя.")
    void updateUser() {
        // given
        User user = UserDtoMapper.mapToUser(makeUserDto("old@mail.ru", "OldName"));
        em.persist(user);
        em.flush();

        UserDto updateDto = new UserDto();
        updateDto.setName("NewName");
        updateDto.setEmail("new@mail.ru");

        // when
        service.updateUser(user.getId(), updateDto);

        // then
        User result = em.find(User.class, user.getId());
        assertThat(result.getName(), equalTo("NewName"));
        assertThat(result.getEmail(), equalTo("new@mail.ru"));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - получение пользователя по id.")
    void getUsetById() {
        // given
        User user = UserDtoMapper.mapToUser(makeUserDto("get@mail.ru", "Get"));
        em.persist(user);
        em.flush();

        // when
        UserDto result = service.getUsetById(user.getId());

        // then
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo("Get"));
        assertThat(result.getEmail(), equalTo("get@mail.ru"));
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - получение пользователя по id (не правильный id).")
    void getUsetById_whenUserNotFound_thenThrowException() {
        // given
        Long userId = 99999L;

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(ru.practicum.exception.NotFoundException.class, () -> {
            service.getUsetById(userId);
        });
    }

    @Test
    @DisplayName("Сервис(интеграционный тест) - удаление пользователя.")
    void deleteUser() {
        // given
        User user = UserDtoMapper.mapToUser(makeUserDto("delete@mail.ru", "Delete"));
        em.persist(user);
        em.flush();
        Long userId = user.getId();

        // when
        service.deleteUser(userId);

        // then
        User deletedUser = em.find(User.class, userId);
        assertThat(deletedUser, nullValue());
    }


    private UserDto makeUserDto(String email, String name) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(name);
        return dto;
    }
}

