package com.n26.validator.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Getter
@AllArgsConstructor
public enum ErrorCategory {
    IRRELEVANT_DATA(NO_CONTENT),
    UNPROCESSABLE_DATA(UNPROCESSABLE_ENTITY);

    private HttpStatus httpStatus;
}
