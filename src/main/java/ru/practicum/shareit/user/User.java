package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {

    long id;

    String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Введен не имейл")
    String email;

}