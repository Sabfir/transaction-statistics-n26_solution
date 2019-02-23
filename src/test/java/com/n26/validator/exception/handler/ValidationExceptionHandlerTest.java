package com.n26.validator.exception.handler;

import com.n26.validator.exception.ValidationException;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.n26.validator.exception.ErrorCategory.IRRELEVANT_DATA;
import static com.n26.validator.exception.ErrorCategory.UNPROCESSABLE_DATA;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ValidationExceptionHandlerTest {
    private ValidationExceptionHandler validationExceptionHandler = new ValidationExceptionHandler();
    private ValidationException input;

    public ValidationExceptionHandlerTest(ValidationException input) {
        this.input = input;
    }

    @Test
    public void handleCommonException() throws Exception {
        final ResponseEntity<?> responseEntity = validationExceptionHandler.handleCommonException(input);
        final HttpStatus actualStatusCode = responseEntity.getStatusCode();
        final HttpStatus expectedHttpStatus = input.getErrorMessage().getCategory().getHttpStatus();

        assertThat("Validation exception returned incorrect HttpStatus", actualStatusCode, Is.is(expectedHttpStatus));

    }

    @Parameterized.Parameters
    public static List testCases() {
        return Arrays.asList(new Object[][] {
                { new ValidationException(UNPROCESSABLE_DATA, "") },
                { new ValidationException(IRRELEVANT_DATA, "") }
                // other positive cases to be added here
        });
    }
}