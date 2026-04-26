package ru.practicum.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserStorageMem implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long count = 0;

    @Override
    public Collection<User> findAllUser() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        count++;
        user.setId(count);
        users.put(count, user);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    @Override
    public User getUsetById(Long id) {
        if (users.get(id) == null) {
            throw new NotFoundException("Такого пользователя нет");
        }
        return users.get(id);
    }
}
