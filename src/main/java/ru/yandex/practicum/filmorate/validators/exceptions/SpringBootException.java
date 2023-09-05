package ru.yandex.practicum.filmorate.validators.exceptions;

import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public class SpringBootException extends ResponseEntityExceptionHandler {
    public SpringBootException(String message) {
        super();
    }
}
