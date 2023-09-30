package ru.yandex.practicum.filmorate.validators.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)                                      // 404
    public ErrorResponse notFound(final ObjectNotFoundException exc) {
        getLog(exc.getStackTrace(), exc.getMessage());
        return new ErrorResponse(getClassStartException(exc.getStackTrace()) + "/" +
            getMethodStartException(exc.getStackTrace()) + exc.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)                                    // 400
    public ErrorResponse badValidation(final ValidationException exc) {
        getLog(exc.getStackTrace(), exc.getMessage());
        return new ErrorResponse(getClassStartException(exc.getStackTrace()) + "/" +
            getMethodStartException(exc.getStackTrace()) + exc.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)                          // 500
    public ErrorResponse badWork(final Exception exc) {
        getLog(exc.getStackTrace(), exc.getMessage());
        return new ErrorResponse(getClassStartException(exc.getStackTrace()) + "/" +
            getMethodStartException(exc.getStackTrace()) + exc.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)                          // 500
    public ErrorResponse badWorkDB(final DataAccessException exc) {
        getLog(exc.getStackTrace(), exc.getMessage());
        return new ErrorResponse(getClassStartException(exc.getStackTrace()) + "/" +
            getMethodStartException(exc.getStackTrace()) + exc.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)                                    // 400
    public ErrorResponse badSBValidationUpLevelController(final ConstraintViolationException exc) {
        getLog(exc.getStackTrace(), exc.getMessage());
        return new ErrorResponse(getClassStartException(exc.getStackTrace()) + "/" +
            getMethodStartException(exc.getStackTrace()) + exc.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)                                    // 400
    public ErrorResponse badSBValidationOnRequestBody(final MethodArgumentNotValidException exc) {
        getLog(exc.getStackTrace(), exc.getMessage());
        return new ErrorResponse(getClassStartException(exc.getStackTrace()) + "/" +
            getMethodStartException(exc.getStackTrace()) + exc.getMessage());
    }

    private String getClassStartException(StackTraceElement[] methods) {
        return methods[3].toString();
    }

    private String getMethodStartException(StackTraceElement[] methods) {
        return methods[2].toString();
    }

    private void getLog(StackTraceElement[] methods, String message) {
        log.info(methods[3].toString() + "/" + methods[2].toString() + ":" + message);
    }
}