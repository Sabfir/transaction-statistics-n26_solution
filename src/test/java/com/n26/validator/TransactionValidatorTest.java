package com.n26.validator;

import com.n26.dto.TransactionUnitDto;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.n26.helper.TestHelper.generateRandomBigDecimalStr;
import static com.n26.helper.TestHelper.generateRandomTimestampWithingLastMinute;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class TransactionValidatorTest {
    private TransactionValidator transactionValidator;
    private TransactionUnitDto input;

    public TransactionValidatorTest(TransactionUnitDto input) {
        this.input = input;
    }

    @Before
    public void setUp() {
        transactionValidator = new TransactionValidator();
    }

    @Test
    public void isValid() throws Exception {
        assertTrue("Error validating correct transaction", transactionValidator.isValid(input, null));
    }

    @Parameterized.Parameters
    public static List testCases() {
        return Arrays.asList(new Object[][] {
                { new TransactionUnitDto(generateRandomBigDecimalStr(1000), generateRandomTimestampWithingLastMinute()) },
                { new TransactionUnitDto(generateRandomBigDecimalStr(100), generateRandomTimestampWithingLastMinute()) },
                { new TransactionUnitDto(generateRandomBigDecimalStr(-1000), generateRandomTimestampWithingLastMinute()) },
                { new TransactionUnitDto(generateRandomBigDecimalStr(1), generateRandomTimestampWithingLastMinute()) }
                // other positive cases to be added here
        });
    }
}