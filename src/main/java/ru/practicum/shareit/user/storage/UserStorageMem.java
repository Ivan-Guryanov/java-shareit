package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserStorageMem implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    long count = 0;

    @Override
    public Collection<UserDto> findAllUser() {
        log.info("Получен запрос на полученние всех пользователей");
        return users.values().stream()
                .map(UserDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(User user) {
        log.info("Получен запрос на добавление пользователя");
        count++;
        user.setId(count);
        users.put(count, user);
        log.info("Пользователь добавлен с id {}", count);
        return UserDtoMapper.mapToDto(user);
    }

    @Override
    public UserDto updateUser(User newUser) {
        log.info("Получен запрос на обновление пользователя с id {}", newUser.getId());
        users.put(newUser.getId(), newUser);
        log.info("Пользователь с id {} успешно обновлен", newUser.getId());
        return UserDtoMapper.mapToDto(newUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Получен запрос на удаление пользователя id {}", id);
        users.remove(id);
        log.info("Пользователь с id {} успешно удален", id);
    }

    @Override
    public UserDto getUsetById(Long id) {
        return UserDtoMapper.mapToDto(users.get(id));
    }
}
