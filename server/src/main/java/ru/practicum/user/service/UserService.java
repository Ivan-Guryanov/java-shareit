package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

import java.util.Collection;


public interface UserService {
    Collection<UserDto> findAllUser();

    UserDto createUser(UserDto user);

    UserDto updateUser(Long id, UserDto newUser);

    void deleteUser(Long id);

    UserDto getUsetById(Long id);
}
