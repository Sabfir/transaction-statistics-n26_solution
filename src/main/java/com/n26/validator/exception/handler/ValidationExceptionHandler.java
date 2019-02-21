package com.n26.validator.exception.handler;

import com.n26.validator.exception.ValidationException;
import com.n26.validator.exception.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@ControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ValidationExceptionHandler {

    @ExceptionHandler(value = ValidationException.class)
    public ResponseEntity handleCommonException(ValidationException exception) {
        final ErrorMessage errorMessage = exception.getErrorMessage();
        log.error("Handled exception with error category: {} and message: {}", errorMessage.getCategory().name(),
                errorMessage.getMessage());
        final HttpStatus httpStatus = errorMessage.getCategory().getHttpStatus();

        return new ResponseEntity<>(httpStatus);
    }
}
