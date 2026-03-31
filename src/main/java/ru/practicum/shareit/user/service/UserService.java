package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;


public interface UserService {
    Collection<UserDto> findAllUser();

    UserDto createUser(User user);

    UserDto updateUser(Long id, User newUser);

    void deleteUser(Long id);

    UserDto getUsetById(Long id);
}
