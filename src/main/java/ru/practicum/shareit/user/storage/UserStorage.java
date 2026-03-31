package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserStorage {
    Collection<UserDto> findAllUser();

    UserDto createUser(User user);

    UserDto updateUser(User newUser);

    void deleteUser(Long id);

    UserDto getUsetById(Long id);
}
