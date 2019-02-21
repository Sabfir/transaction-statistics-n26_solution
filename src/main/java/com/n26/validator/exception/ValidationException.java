package com.n26.validator.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final ErrorMessage errorMessage;

    public ValidationException(ErrorCategory errorCategory, Exception e) {
        this(errorCategory, "", e);
    }

    public ValidationException(ErrorCategory errorCategory, String message) {
        super();
        this.errorMessage = new ErrorMessage(errorCategory, message);
    }

    public ValidationException(ErrorCategory errorCategory, String message, Exception e) {
        super(e);
        final String description = message + " [" + e.getMessage() + "]";
        this.errorMessage = new ErrorMessage(errorCategory, description);
    }
}
