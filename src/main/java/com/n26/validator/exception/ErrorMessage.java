package com.n26.validator.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ErrorMessage {
    private ErrorCategory category;
    private String message;
}
