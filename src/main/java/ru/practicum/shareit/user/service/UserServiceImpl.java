package ru.practicum.shareit.user.service;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private  final UserRepository userRepository;
    private final Validator validator;

    @Transactional
    public Collection<UserDto> findAllUser() {
        return userRepository.findAll().stream()
                .map(UserDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto createUser(UserDto user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new ConflictException("Емейл уже используется");
        }
        User newUser = UserDtoMapper.mapToUser(user);
        User savedUser = userRepository.save(newUser);
        return UserDtoMapper.mapToDto(savedUser);
    }

    @Transactional
    public  UserDto updateUser(Long id, UserDto newUser) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (userRepository.findByEmail(newUser.getEmail()) != null) {
            throw new ConflictException("Емейл уже используется");
        }
        newUser.setId(id);

        if (newUser.getName() == null) {
            newUser.setName(user.getName());
        }
        if (newUser.getEmail() == null) {
            newUser.setEmail(user.getEmail());
        }

        User updateUser = UserDtoMapper.mapToUser(newUser);

        return UserDtoMapper.mapToDto(userRepository.save(updateUser));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public UserDto getUsetById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return  UserDtoMapper.mapToDto(user);
    }

}
