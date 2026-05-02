package ru.practicum.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.user.User;


public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
}