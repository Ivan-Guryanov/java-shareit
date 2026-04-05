package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class User {

    private long id;

    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Введен не имейл")
    private String email;

}