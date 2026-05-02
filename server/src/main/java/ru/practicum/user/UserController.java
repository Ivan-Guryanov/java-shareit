package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto user) {
        log.info("Получен запрос на добавление пользователя");
        UserDto createUser = userService.createUser(user);
        log.info("Пользователь добавлен с id {}", createUser.getId());
        return createUser;
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto newUser) {
        log.info("Получен запрос на обновление пользователя с id {}", id);
        UserDto updateUser = userService.updateUser(id, newUser);
        log.info("Пользователь с id {} успешно обновлен", updateUser.getId());
        return updateUser;
    }

    @GetMapping("/{id}")
    public UserDto getUsetById(@PathVariable Long id) {
        log.info("Получен запрос на получение пользователя с id {}", id);
        UserDto getUsetById = userService.getUsetById(id);
        log.info("Пользователь с id {} успешно получен", getUsetById.getId());
        return getUsetById;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Получен запрос на удаление пользователя id {}", id);
        userService.deleteUser(id);
        log.info("Пользователь с id {} успешно удален", id);

    }

}