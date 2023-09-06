package ru.yandex.practicum.filmorate.validators.exceptions;

import lombok.Data;

@Data public class ErrorResponse {
    String error;

    ErrorResponse(String error) {
        this.error = error;
    }
}
