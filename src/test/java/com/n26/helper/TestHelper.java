package com.n26.helper;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Random;

import static com.n26.validator.TransactionValidator.DEFAULT_ZONE;
import static com.n26.validator.TransactionValidator.TIMESTAMP_FORMATTER;

public class TestHelper {
    private TestHelper(){}

    public static String generateRandomBigDecimalStr(long bound) {
        return generateRandomBigDecimal(bound).toString();
    }

    public static BigDecimal generateRandomBigDecimal(long bound) {
        return BigDecimal.valueOf(new Random().nextDouble() * bound);
    }

    public static String generateRandomTimestampWithingLastMinuteStr() {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC()).minusSeconds(new Random().nextInt(50));
        ZonedDateTime nowZoned =  now.atZone(DEFAULT_ZONE);
        return nowZoned.format(TIMESTAMP_FORMATTER);
    }

    public static String generateRandomOldTimestampStr() {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC()).minusMinutes(new Random().nextInt(1000 - 1) + 1);
        ZonedDateTime nowZoned =  now.atZone(DEFAULT_ZONE);
        return nowZoned.format(TIMESTAMP_FORMATTER);
    }

    public static LocalDateTime generateRandomOldTimestamp() {
        return LocalDateTime.now(Clock.systemUTC()).minusMinutes(new Random().nextInt(1000 - 1) + 1);
    }

    public static String generateRandomFutureTimestampStr() {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC()).plusSeconds(new Random().nextInt(100000 - 1) + 1);
        ZonedDateTime nowZoned =  now.atZone(DEFAULT_ZONE);
        return nowZoned.format(TIMESTAMP_FORMATTER);
    }
}
