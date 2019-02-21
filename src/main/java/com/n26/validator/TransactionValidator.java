package com.n26.validator;

import com.n26.dto.TransactionUnitDto;
import com.n26.validator.exception.ValidationException;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import static com.n26.service.TransactionServiceImpl.TIME_TO_STORE_IN_SEC;
import static com.n26.validator.exception.ErrorCategory.IRRELEVANT_DATA;
import static com.n26.validator.exception.ErrorCategory.UNPROCESSABLE_DATA;
import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
public class TransactionValidator implements ConstraintValidator<CheckTransaction, TransactionUnitDto> {
    public static final ZoneId DEFAULT_ZONE = ZoneId.of("UTC");
    public static final DateTimeFormatter TIMESTAMP_FORMATTER =
            ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(DEFAULT_ZONE);

    @Override
    public void initialize(CheckTransaction constraintAnnotation) {}

    // TODO OPINTA: test it
    @Override
    public boolean isValid(TransactionUnitDto transactionUnitDto, ConstraintValidatorContext context) {
        checkAmount(transactionUnitDto.getAmount());
        checkTimestamp(transactionUnitDto.getTimestamp());
        return true;
    }

    private void checkAmount(String amount) {
        if (isBlank(amount)) {
            throw new ValidationException(UNPROCESSABLE_DATA, "Transaction amount can't be empty");
        }
        try {
            new BigDecimal(amount);
        } catch (NumberFormatException e) {
            throw new ValidationException(UNPROCESSABLE_DATA, format("Can't parse transaction's amount %s", amount), e);
        }
    }

    private void checkTimestamp(String timestamp) {
        if (isBlank(timestamp)) {
            throw new ValidationException(UNPROCESSABLE_DATA, "Transaction timestamp can't be empty");
        }

        final LocalDateTime dateTimeValue;
        try {
            dateTimeValue = LocalDateTime.parse(timestamp, TIMESTAMP_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ValidationException(UNPROCESSABLE_DATA,
                    format("Can't parse transaction's timestamp %s", timestamp), e);
        }

        LocalDateTime utcNow = LocalDateTime.now(Clock.systemUTC());
        final long createdSecondsAgo = Duration.between(dateTimeValue, utcNow).getSeconds();
        if (createdSecondsAgo > TIME_TO_STORE_IN_SEC - 1) {
            throw new ValidationException(IRRELEVANT_DATA, "Transaction is too old");
        } else if (createdSecondsAgo < 0) {
            throw new ValidationException(UNPROCESSABLE_DATA, "Future transaction can't be processed");
        }
    }
}
