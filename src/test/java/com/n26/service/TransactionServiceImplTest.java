package com.n26.service;

import com.n26.dto.TransactionStatisticDto;
import com.n26.dto.TransactionUnitDto;
import com.n26.helper.TestHelper;
import com.n26.mapper.TransactionStatisticMapper;
import com.n26.mapper.TransactionUnitMapper;
import com.n26.model.CalculatedUnit;
import com.n26.model.TransactionStatistic;
import com.n26.model.TransactionUnit;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.n26.helper.TestHelper.generateRandomOldTimestamp;
import static com.n26.service.TransactionServiceImpl.TIME_TO_STORE_IN_SEC;
import static com.n26.validator.TransactionValidator.DEFAULT_ZONE;
import static com.n26.validator.TransactionValidator.TIMESTAMP_FORMATTER;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TransactionServiceImplTest {
    private TransactionServiceImpl transactionService;
    @Mock
    private TransactionUnitMapper transactionUnitMapper;
    @Mock
    private TransactionStatisticMapper transactionStatisticMapper;

    @Before
    public void setUp() {
        initMocks(this);
        transactionService = new TransactionServiceImpl(transactionUnitMapper, transactionStatisticMapper);
    }

    @Test
    public void getStatistic() throws Exception {
        // add few elements, check expected statistics
        // given
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        ZonedDateTime earlier =  now.minusSeconds(10).atZone(DEFAULT_ZONE);
        TransactionUnitDto inputTransactionDto = new TransactionUnitDto("1", earlier.format(TIMESTAMP_FORMATTER));
        TransactionUnit inputTransaction = new TransactionUnit(ONE, earlier.toLocalDateTime());
        when(transactionUnitMapper.toEntity(inputTransactionDto)).thenReturn(inputTransaction);
        transactionService.create(inputTransactionDto);

        ZonedDateTime nowZoned =  now.atZone(DEFAULT_ZONE);
        inputTransactionDto = new TransactionUnitDto("1O", nowZoned.format(TIMESTAMP_FORMATTER));
        inputTransaction = new TransactionUnit(TEN, nowZoned.toLocalDateTime());
        when(transactionUnitMapper.toEntity(inputTransactionDto)).thenReturn(inputTransaction);
        transactionService.create(inputTransactionDto);

        final BigDecimal overallSum = TEN.add(ONE);
        final BigDecimal overallMax = TEN;
        final BigDecimal overallMin = ONE;
        final long overallCount = 2;
        final BigDecimal average = overallSum.divide(new BigDecimal(overallCount), 2, HALF_UP);

        // when
        final TransactionStatisticDto transactionStatisticDto = new TransactionStatisticDto(
                overallSum.toString(), average.toString(), overallMax.toString(), overallMin.toString(), overallCount);
        when(transactionStatisticMapper.toDto(any(TransactionStatistic.class))).thenReturn(transactionStatisticDto);
        final TransactionStatisticDto statistic = transactionService.getStatistic();

        // then
        assertThat("The statistic's sum is not correct", statistic.getSum(), is(overallSum.toString()));
        assertThat("The statistic's max is not correct", statistic.getMax(), is(overallMax.toString()));
        assertThat("The statistic's min is not correct", statistic.getMin(), is(overallMin.toString()));
        assertThat("The transactions added is not correct", statistic.getCount(), is(2L));
        assertThat("The statistic's average is not correct", statistic.getAvg(), is(average.toString()));
    }

    @Test
    public void getStatisticOnEmptyQueue() throws Exception {
        // when
        final TransactionStatisticDto expected = new TransactionStatisticDto("0.00", "0.00", "0.00", "0.00", 0L);
        when(transactionStatisticMapper.toDto(any(TransactionStatistic.class))).thenReturn(expected);
        final TransactionStatisticDto statistic = transactionService.getStatistic();

        // then
        assertThat("The statistic's sum is not correct", statistic.getSum(), is("0.00"));
        assertThat("The statistic's max is not correct", statistic.getMax(), is("0.00"));
        assertThat("The statistic's min is not correct", statistic.getMin(), is("0.00"));
        assertThat("The transactions added is not correct", statistic.getCount(), is(0L));
        assertThat("The statistic's average is not correct", statistic.getAvg(), is("0.00"));
    }

    @Test
    public void created() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        ZonedDateTime earlier =  now.minusSeconds(10).atZone(DEFAULT_ZONE);
        TransactionUnitDto inputTransactionDto = new TransactionUnitDto("10", earlier.format(TIMESTAMP_FORMATTER));
        TransactionUnit inputTransaction = new TransactionUnit(ONE, earlier.toLocalDateTime());
        CalculatedUnit expectedResultTen = new CalculatedUnit(ONE, ONE, ONE, 1);

        // when
        when(transactionUnitMapper.toEntity(inputTransactionDto)).thenReturn(inputTransaction);
        transactionService.create(inputTransactionDto);

        // then
        List<CalculatedUnit> actualDataList = transactionService.getNotEmptyRawData();
        assertThat("The size of created transactions is incorrect", actualDataList.size(), is(1));
        CalculatedUnit actualResult = actualDataList.get(0);
        assertThat("The expected element of the queue is incorrect", actualResult, is(expectedResultTen));
    }

    @Test
    public void createdTransactionsAreStoredInCorrectOrder() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        ZonedDateTime earlier =  now.minusSeconds(10).atZone(DEFAULT_ZONE);
        TransactionUnitDto inputTransactionDto = new TransactionUnitDto("10", earlier.format(TIMESTAMP_FORMATTER));
        TransactionUnit inputTransaction = new TransactionUnit(ONE, earlier.toLocalDateTime());
        CalculatedUnit expectedResultOne = new CalculatedUnit(ONE, ONE, ONE, 1);

        // when
        when(transactionUnitMapper.toEntity(inputTransactionDto)).thenReturn(inputTransaction);
        transactionService.create(inputTransactionDto);

        // given
        ZonedDateTime nowZoned =  now.atZone(DEFAULT_ZONE);
        inputTransactionDto = new TransactionUnitDto("1", nowZoned.format(TIMESTAMP_FORMATTER));
        inputTransaction = new TransactionUnit(TEN, nowZoned.toLocalDateTime());
        CalculatedUnit expectedResultTen = new CalculatedUnit(TEN, TEN, TEN, 1);

        // when
        when(transactionUnitMapper.toEntity(inputTransactionDto)).thenReturn(inputTransaction);
        transactionService.create(inputTransactionDto);

        // then
        List<CalculatedUnit> createdData = transactionService.getNotEmptyRawData();
        assertThat("The size of created transactions is incorrect", createdData.size(), is(2));
        CalculatedUnit actualResult = createdData.get(0);
        assertThat("The first element should be the first added", actualResult, is(expectedResultOne));
        actualResult = createdData.get(1);
        assertThat("The last element should be the last added", actualResult, is(expectedResultTen));
    }

    @Test
    public void oldTransactionShouldNotBeStored() throws Exception {
        // when
        when(transactionUnitMapper.toEntity(any(TransactionUnitDto.class))).thenReturn(new TransactionUnit(TEN, generateRandomOldTimestamp()));
        transactionService.create(new TransactionUnitDto("10", TestHelper.generateRandomOldTimestampStr()));
        // then
        assertThat("Old transaction should not be stored", transactionService.getNotEmptyRawData().size(), is(0));
    }

    @Test
    public void removeAll() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        ZonedDateTime earlier =  now.minusSeconds(10).atZone(DEFAULT_ZONE);
        TransactionUnitDto inputTransactionDto = new TransactionUnitDto("10", earlier.format(TIMESTAMP_FORMATTER));
        TransactionUnit inputTransaction = new TransactionUnit(ONE, earlier.toLocalDateTime());
        when(transactionUnitMapper.toEntity(inputTransactionDto)).thenReturn(inputTransaction);
        transactionService.create(inputTransactionDto);

        ZonedDateTime nowZoned =  now.atZone(DEFAULT_ZONE);
        inputTransactionDto = new TransactionUnitDto("1", nowZoned.format(TIMESTAMP_FORMATTER));
        inputTransaction = new TransactionUnit(TEN, nowZoned.toLocalDateTime());
        when(transactionUnitMapper.toEntity(inputTransactionDto)).thenReturn(inputTransaction);
        transactionService.create(inputTransactionDto);

        List<CalculatedUnit> createdData = transactionService.getNotEmptyRawData();
        assertThat("The size of created transactions is incorrect", createdData.size(), is(2));
        // when
        transactionService.removeAll();
        // then
        createdData = transactionService.getNotEmptyRawData();
        assertThat("The size of created transactions has not been removed", createdData.size(), is(0));
    }

    @Test
    public void ticShouldNotChangeTheSizeOfTheQueue() throws Exception {
        // check initial state
        assertThat("The initial size of the queue is incorrect",
                TIME_TO_STORE_IN_SEC, is(transactionService.getRawData().size()));
        assertThat("The initial queue should have only empty elements",
                0, is(transactionService.getNotEmptyRawData().size()));

        // check tic
        transactionService.tic();
        assertThat("The size of the queue after ticking should remain the same",
                TIME_TO_STORE_IN_SEC, is(transactionService.getRawData().size()));
    }

    @Test
    public void ticShouldWorkInFifo() throws Exception {
        // prepare data
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        ZonedDateTime nowZoned =  now.atZone(DEFAULT_ZONE);
        TransactionUnitDto inputTransactionDto = new TransactionUnitDto("10", nowZoned.format(TIMESTAMP_FORMATTER));
        final TransactionUnit inputTransactionTen = new TransactionUnit(TEN, nowZoned.toLocalDateTime());
        when(transactionUnitMapper.toEntity(inputTransactionDto)).thenReturn(inputTransactionTen);
        transactionService.create(inputTransactionDto);

        ZonedDateTime earlier =  now.minusSeconds(10).atZone(DEFAULT_ZONE);
        inputTransactionDto = new TransactionUnitDto("1", earlier.format(TIMESTAMP_FORMATTER));
        final TransactionUnit inputTransactionOne = new TransactionUnit(ONE, earlier.toLocalDateTime());
        when(transactionUnitMapper.toEntity(inputTransactionDto)).thenReturn(inputTransactionOne);
        transactionService.create(inputTransactionDto);

        List<CalculatedUnit> rawData = transactionService.getRawData();

        final int seqNumOfTen = getIndexInQueue(inputTransactionTen, rawData);
        assertThat("Can't find added element in the queue", seqNumOfTen, not(-1));
        final int seqNumOfOne = getIndexInQueue(inputTransactionOne, rawData);
        assertThat("Can't find added element in the queue", seqNumOfOne, not(-1));

        // check tic should remove tail, add head and move down body
        int tickTimes = new Random().nextInt(20 - 1) + 1;
        IntStream.range(0, tickTimes).forEach((n) -> transactionService.tic());

        rawData = transactionService.getRawData();

        final int actualSeqNumOfTenAfterTic = getIndexInQueue(inputTransactionTen, rawData);
        assertThat("Can't find added element in the queue after tic", actualSeqNumOfTenAfterTic, not(-1));
        final int actualSeqNumOfOneAfterTic = getIndexInQueue(inputTransactionOne, rawData);
        assertThat("Can't find added element in the queue after tic", actualSeqNumOfOneAfterTic, not(-1));

        final int expectedSeqNumOfTenAfterTic = seqNumOfTen - tickTimes;
        final int expectedSeqNumOfOneAfterTic = seqNumOfOne - tickTimes;

        assertThat("The expected sequential number of the element after ticking is not correct",
                actualSeqNumOfTenAfterTic, is(expectedSeqNumOfTenAfterTic));
        assertThat("The expected sequential number of the element after ticking is not correct",
                actualSeqNumOfOneAfterTic, is(expectedSeqNumOfOneAfterTic));
    }

    private int getIndexInQueue(TransactionUnit inputTransactionTen, List<CalculatedUnit> rawData) {
        return IntStream.range(0, rawData.size())
                .filter(i -> rawData.get(i).getSum().equals(inputTransactionTen.getAmount()))
                .findAny().orElse(-1);
    }
}