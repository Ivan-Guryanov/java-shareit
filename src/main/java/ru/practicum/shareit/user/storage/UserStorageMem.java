package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserStorageMem implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    long count = 0;

    @Override
    public Collection<UserDto> findAllUser() {
        return users.values().stream()
                .map(UserDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(User user) {
        count++;
        user.setId(count);
        users.put(count, user);
        return UserDtoMapper.mapToDto(user);
    }

    @Override
    public UserDto updateUser(User newUser) {
        users.put(newUser.getId(), newUser);
        return UserDtoMapper.mapToDto(newUser);
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    @Override
    public UserDto getUsetById(Long id) {
        if (users.get(id) == null) {
            throw new NotFoundException("Такого пользователя нет");
        }
        return UserDtoMapper.mapToDto(users.get(id));
    }
}
