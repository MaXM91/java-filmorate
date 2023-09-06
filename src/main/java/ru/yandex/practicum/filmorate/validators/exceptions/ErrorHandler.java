package ru.yandex.practicum.filmorate.validators.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)                                      // 404
    public ErrorResponse notFound(final ObjectNotFoundException exc) {
        return new ErrorResponse(exc.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)                                    // 400
    public ErrorResponse badValidation(final ValidationException exc) {
        return new ErrorResponse(exc.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)                          // 500
    public ErrorResponse badWork(final Exception exc) {
        return new ErrorResponse(exc.getMessage());
    }
}
