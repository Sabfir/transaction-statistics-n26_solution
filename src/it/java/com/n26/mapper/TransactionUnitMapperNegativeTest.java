package com.n26.mapper;

import com.n26.dto.TransactionUnitDto;
import com.n26.model.TransactionUnit;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import static com.n26.validator.TransactionValidator.DEFAULT_ZONE;
import static com.n26.validator.TransactionValidator.TIMESTAMP_FORMATTER;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TransactionUnitMapperNegativeTest {
    private static final String DEFAULT_TIMESTAMP = "2019-02-18T09:55:35.312Z";
    private TransactionUnitMapper transactionUnitMapper;

    @Before
    public void setUp() {
        transactionUnitMapper = Mappers.getMapper(TransactionUnitMapper.class);
    }

    @Test(expected = DateTimeParseException.class)
    public void parseEmptyTimestampShouldThrowException() throws Exception {
        String timestamp = "";
        transactionUnitMapper.toLocalDateTime(timestamp);
    }

    @Test(expected = NullPointerException.class)
    public void parseNullTimestampShouldThrowException() throws Exception {
        String timestamp = null;
        transactionUnitMapper.toLocalDateTime(timestamp);
    }

    @Test(expected = NumberFormatException.class)
    public void parseAmountWithIncorrectFormatShouldThrowException() throws Exception {
        final TransactionUnitDto dto = new TransactionUnitDto("10E", DEFAULT_TIMESTAMP);
        transactionUnitMapper.toEntity(dto);
    }

    @Test(expected = NumberFormatException.class)
    public void parseEmptyAmountShouldThrowException() throws Exception {
        final TransactionUnitDto dto = new TransactionUnitDto("", DEFAULT_TIMESTAMP);
        transactionUnitMapper.toEntity(dto);
    }
}