package com.n26.mapper;

import com.n26.dto.TransactionStatisticDto;
import com.n26.helper.TestHelper;
import com.n26.model.TransactionStatistic;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mapstruct.factory.Mappers;

import static com.n26.helper.TestHelper.generateRandomBigDecimal;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class TransactionStatisticMapperTest {
    private TransactionStatisticMapper transactionStatisticMapper;
    private TransactionStatistic input;
    private TransactionStatisticDto expectedResult;

    public TransactionStatisticMapperTest(TransactionStatistic input, TransactionStatisticDto expectedResult) {
        this.input = input;
        this.expectedResult = expectedResult;
    }

    @Before
    public void setUp() {
        transactionStatisticMapper = Mappers.getMapper(TransactionStatisticMapper.class);
    }

    @Test
    public void toDto() throws Exception {
        final TransactionStatisticDto actualResult = transactionStatisticMapper.toDto(input);
        assertThat("Actual mapped statistic doesn't match expected", actualResult, is(expectedResult));
    }

    @Parameterized.Parameters
    public static List testCases() {
        final List<Object[]> inputData = new ArrayList<>();
        // some critical data
        inputData.add(new Object[] {null, null});
        inputData.add(new Object[] {new TransactionStatistic(null, null, null, null, 0), new TransactionStatisticDto(null, null, null, null, 0)});
        inputData.add(new Object[] {new TransactionStatistic(ZERO, ZERO, ZERO, ZERO, 0), new TransactionStatisticDto("0", "0", "0", "0", 0)});
        // and some random data
        TransactionStatistic random = createRandomTransactionStatistic();
        inputData.add(new Object[] {random, toDto(random)});
        random = createRandomTransactionStatistic();
        inputData.add(new Object[] {random, toDto(random)});
        random = createRandomTransactionStatistic();
        inputData.add(new Object[] {random, toDto(random)});
        // other positive cases to be added here

        return inputData;
    }

    private static TransactionStatistic createRandomTransactionStatistic() {
        BigDecimal sum = generateRandomBigDecimal(100000000000L);
        BigDecimal max = generateRandomBigDecimal(10000);
        BigDecimal min = generateRandomBigDecimal(1);
        BigDecimal average = generateRandomBigDecimal(100);
        long count = (long) (Math.random() * 100);
        final TransactionStatistic model = new TransactionStatistic(sum, max, min, average, count);
        final TransactionStatisticDto dto = new TransactionStatisticDto(
                sum.toString(), average.toString(), max.toString(), min.toString(), count);
        return model;
    }

    private static TransactionStatisticDto toDto(TransactionStatistic model) {
        return new TransactionStatisticDto(
                model.getSum().toString(),
                model.getAverage().toString(),
                model.getMax().toString(),
                model.getMin().toString(),
                model.getCount()
        );
    }
}