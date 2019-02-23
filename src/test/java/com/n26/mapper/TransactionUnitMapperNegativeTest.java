package com.n26.mapper;

import com.n26.dto.TransactionUnitDto;
import java.time.format.DateTimeParseException;
import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

public class TransactionUnitMapperNegativeTest {
    private static final String DEFAULT_TIMESTAMP = "2019-02-18T09:55:35.312Z";
    private TransactionUnitMapper transactionUnitMapper;

    @Before
    public void setUp() {
        transactionUnitMapper = Mappers.getMapper(TransactionUnitMapper.class);
    }

    @Test(expected = DateTimeParseException.class)
    public void parseEmptyTimestampShouldThrowException() throws Exception {
        transactionUnitMapper.toLocalDateTime("");
    }

    @Test(expected = NullPointerException.class)
    public void parseNullTimestampShouldThrowException() throws Exception {
        transactionUnitMapper.toLocalDateTime(null);
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