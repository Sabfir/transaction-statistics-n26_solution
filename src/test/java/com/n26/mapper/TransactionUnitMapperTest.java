package com.n26.mapper;

import com.n26.dto.TransactionUnitDto;
import com.n26.model.TransactionUnit;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mapstruct.factory.Mappers;

import static com.n26.validator.TransactionValidator.TIMESTAMP_FORMATTER;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class TransactionUnitMapperTest {
    private TransactionUnitMapper transactionUnitMapper;
    private TransactionUnitDto input;
    private TransactionUnit expectedResult;

    public TransactionUnitMapperTest(TransactionUnitDto input, TransactionUnit expectedResult) {
        this.input = input;
        this.expectedResult = expectedResult;
    }

    @Before
    public void setUp() {
        transactionUnitMapper = Mappers.getMapper(TransactionUnitMapper.class);
    }

    @Test
    public void toEntity() throws Exception {
        final TransactionUnit actualResult = transactionUnitMapper.toEntity(input);
        assertThat("Actual mapped transaction doesn't match expected", actualResult, is(expectedResult));
    }

    @Parameterized.Parameters
    public static List testCases() {

        return Arrays.asList(new Object[][] {
                { null, null },
                { new TransactionUnitDto("1", "2019-02-18T09:55:35.312Z"), new TransactionUnit(ONE, LocalDateTime.parse("2019-02-18T09:55:35.312Z", TIMESTAMP_FORMATTER)) },
                { new TransactionUnitDto("100", "2022-02-18T10:55:35.312Z"), new TransactionUnit(new BigDecimal("100"), LocalDateTime.parse("2022-02-18T10:55:35.312Z", TIMESTAMP_FORMATTER)) },
                { new TransactionUnitDto("-5", "2019-02-19T10:55:35.312Z"), new TransactionUnit(new BigDecimal("-5"), LocalDateTime.parse("2019-02-19T10:55:35.312Z", TIMESTAMP_FORMATTER)) },
                { new TransactionUnitDto("0", "2019-02-18T10:55:35.312Z"), new TransactionUnit(ZERO, LocalDateTime.parse("2019-02-18T10:55:35.312Z", TIMESTAMP_FORMATTER)) }
                // other positive cases to be added here
        });
    }
}