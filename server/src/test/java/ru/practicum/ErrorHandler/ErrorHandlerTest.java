package ru.practicum.ErrorHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ErrorHandler;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ErrorHandlerTest {

    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    @DisplayName("ErrorHandler — проверка NotFoundException (404)")
    void handleNotFound() {
        // given
        NotFoundException ex = new NotFoundException("User not found");

        // when
        Map<String, String> response = errorHandler.handleNotFoundException(ex);

        // then
        assertThat(response)
                .isNotNull()
                .containsEntry("error", "User not found");
    }

    @Test
    @DisplayName("ErrorHandler — проверка ValidationException (400)")
    void handleValidation() {
        // given
        ValidationException ex = new ValidationException("Invalid email");

        // when
        Map<String, String> response = errorHandler.handleValidationException(ex);

        // then
        assertThat(response)
                .isNotNull()
                .containsEntry("error", "Ошибка валидации")
                .containsEntry("message", "Invalid email");
    }

    @Test
    @DisplayName("ErrorHandler — проверка ConflictException (409)")
    void handleConflict() {
        // given
        ConflictException ex = new ConflictException("Email already exists");

        // when
        Map<String, String> response = errorHandler.handleConflictException(ex);

        // then
        assertThat(response)
                .isNotNull()
                .containsEntry("error", "Email already exists");
    }

    @Test
    @DisplayName("ErrorHandler — проверка Throwable (500)")
    void handleThrowable() {
        // given
        RuntimeException ex = new RuntimeException("Unexpected error");

        // when
        Map<String, String> response = errorHandler.handleThrowable(ex);

        // then
        assertThat(response)
                .isNotNull()
                .containsEntry("error", "Произошла непредвиденная ошибка на сервере");
    }

    @Test
    @DisplayName("ErrorHandler — проверка DataIntegrityViolationException")
    void handleDataIntegrityViolation() {
        // given
        Throwable rootCause = new RuntimeException("DB error: duplicate entry");

        DataIntegrityViolationException ex = new DataIntegrityViolationException("Main message", rootCause);

        // when
        Map<String, String> response = errorHandler.handleDataIntegrityViolation(ex);

        // then
        assertThat(response)
                .isNotNull()
                .containsEntry("error", "Ошибка целостности данных в базе")
                .containsEntry("details", "DB error: duplicate entry");
    }
}
