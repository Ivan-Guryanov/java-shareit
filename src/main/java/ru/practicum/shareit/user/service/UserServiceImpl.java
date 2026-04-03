package ru.practicum.shareit.user.service;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private  final UserStorage userStorage;
    private final Validator validator;

    public Collection<UserDto> findAllUser() {
        return userStorage.findAllUser();
    }

    public UserDto createUser(User user) {
        var violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ValidationException("Не корректный email - " + user.getEmail());
        }
        if (existsByEmail(user.getEmail())) {
            throw new ConflictException("Email уже занят");
        }
        return userStorage.createUser(user);
    }

    public  UserDto updateUser(Long id, User newUser) {
        UserDto user = userStorage.getUsetById(id);
        newUser.setId(id);
        if (existsByEmail(newUser.getEmail())) {
            throw new ConflictException("Email уже занят");
        }
        if (newUser.getName() == null) {
            newUser.setName(user.getName());
        }
        if (newUser.getEmail() == null) {
            newUser.setEmail(user.getEmail());
        }
        var violations = validator.validate(newUser);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return userStorage.updateUser(newUser);
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    public UserDto getUsetById(Long id) {
        return  userStorage.getUsetById(id);
    }

    public boolean existsByEmail(String email) {
        return userStorage.findAllUser().stream()
                .anyMatch(userDto -> userDto.getEmail().equalsIgnoreCase(email));
    }
}
