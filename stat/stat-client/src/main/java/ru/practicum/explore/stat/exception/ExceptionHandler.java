package ru.practicum.explore.stat.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;


@Slf4j
@RestControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler({InvalidExistException.class,})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIncorrectFilmException(final RuntimeException e) {
        log.warn(e.getMessage(), e);
        return new ErrorResponse(HttpStatus.NOT_FOUND, "The required object was not found.",
                e.getMessage(),
                LocalDateTime.now(), e.getStackTrace().toString());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler({InvalidRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidRequestException(final RuntimeException e) {
        log.warn(e.getMessage(), e);
        return new ErrorResponse(HttpStatus.BAD_REQUEST,"Incorrectly made request.", e.getMessage(),
                LocalDateTime.now(), e.getStackTrace().toString());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler()
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleExceptionValidate(DataIntegrityViolationException e) {
        log.warn("Error", e);
        return new ErrorResponse(HttpStatus.CONFLICT, "Integrity constraint has been violated",
                e.getMessage(), LocalDateTime.now(), e.getStackTrace().toString());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception e) {
        log.warn("Error", e);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e.getMessage(),
                LocalDateTime.now(), e.getStackTrace().toString());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler()
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleExceptionValidate(ConflictException e) {
        log.warn("Error", e);
        return new ErrorResponse(HttpStatus.CONFLICT, "For the requested operation the conditions are not met.",
                e.getMessage(), LocalDateTime.now(), e.getStackTrace().toString());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status);
        body.put("reason", "Incorrectly made request.");
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now().format(formatter));
        return new ResponseEntity<>(body, headers, status);
    }
}
