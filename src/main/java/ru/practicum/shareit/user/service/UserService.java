package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;


public interface UserService {
    Collection<UserDto> findAllUser();

    UserDto createUser(UserDto user);

    UserDto updateUser(Long id, UserDto newUser);

    void deleteUser(Long id);

    UserDto getUsetById(Long id);
}
