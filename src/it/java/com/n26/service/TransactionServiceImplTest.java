package com.n26.service;

import com.n26.dto.TransactionUnitDto;
import com.n26.mapper.TransactionStatisticMapper;
import com.n26.mapper.TransactionUnitMapper;
import com.n26.model.CalculatedUnit;
import com.n26.model.TransactionUnit;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.n26.validator.TransactionValidator.DEFAULT_ZONE;
import static com.n26.validator.TransactionValidator.TIMESTAMP_FORMATTER;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TransactionServiceImplTest {
    private static final String DEFAULT_TIMESTAMP = "2019-02-18T09:55:35.312Z";
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
    public void tic() throws Exception {
        // before tic queue should have size 60, notEmpty=0
        // add new element -> queue=60, notEmpty=1
        // after 60 tics queue=60,notEmpty=0
    }

}