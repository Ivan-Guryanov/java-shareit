package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAllUser();

    User createUser(User user);

    User updateUser(User newUser);

    void deleteUser(Long id);

    User getUsetById(Long id);
}
