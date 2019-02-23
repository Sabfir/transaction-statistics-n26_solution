package com.n26.validator;

import com.n26.dto.TransactionUnitDto;
import com.n26.helper.CustomErrorCategoryMatcher;
import com.n26.validator.exception.ErrorCategory;
import com.n26.validator.exception.ValidationException;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.n26.helper.TestHelper.generateRandomBigDecimalStr;
import static com.n26.helper.TestHelper.generateRandomFutureTimestamp;
import static com.n26.helper.TestHelper.generateRandomOldTimestamp;
import static com.n26.helper.TestHelper.generateRandomTimestampWithingLastMinute;
import static com.n26.validator.exception.ErrorCategory.IRRELEVANT_DATA;
import static com.n26.validator.exception.ErrorCategory.UNPROCESSABLE_DATA;

@RunWith(Parameterized.class)
public class TransactionValidatorNegativeTest {
    private TransactionValidator transactionValidator = new TransactionValidator();
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private TransactionUnitDto input;
    private ErrorCategory expected;

    public TransactionValidatorNegativeTest(TransactionUnitDto input, ErrorCategory expected) {
        this.input = input;
        this.expected = expected;
    }

    @Before
    public void setUp() {
        thrown.expect(ValidationException.class);
        thrown.expect(CustomErrorCategoryMatcher.hasCategory(expected));
    }

    @Test
    public void isNotValid() throws Exception {
        transactionValidator.isValid(input, null);
    }

    @Parameterized.Parameters
    public static List testCases() {
        return Arrays.asList(new Object[][] {
                { null, UNPROCESSABLE_DATA },
                { new TransactionUnitDto(null, generateRandomTimestampWithingLastMinute()), UNPROCESSABLE_DATA },
                { new TransactionUnitDto("", generateRandomTimestampWithingLastMinute()), UNPROCESSABLE_DATA },
                { new TransactionUnitDto(" ", generateRandomTimestampWithingLastMinute()), UNPROCESSABLE_DATA },
                { new TransactionUnitDto("unparsable", generateRandomTimestampWithingLastMinute()), UNPROCESSABLE_DATA },
                { new TransactionUnitDto(generateRandomBigDecimalStr(1), null), UNPROCESSABLE_DATA },
                { new TransactionUnitDto(generateRandomBigDecimalStr(1), ""), UNPROCESSABLE_DATA },
                { new TransactionUnitDto(generateRandomBigDecimalStr(1), " "), UNPROCESSABLE_DATA },
                { new TransactionUnitDto(generateRandomBigDecimalStr(1), "2000-01-01"), UNPROCESSABLE_DATA },
                { new TransactionUnitDto(generateRandomBigDecimalStr(1), generateRandomOldTimestamp()), IRRELEVANT_DATA },
                { new TransactionUnitDto(generateRandomBigDecimalStr(1), generateRandomOldTimestamp()), IRRELEVANT_DATA},
                { new TransactionUnitDto(generateRandomBigDecimalStr(1), generateRandomFutureTimestamp()), UNPROCESSABLE_DATA },
                { new TransactionUnitDto(generateRandomBigDecimalStr(1), generateRandomFutureTimestamp()), UNPROCESSABLE_DATA },
                // other positive cases to be added here
        });
    }
}