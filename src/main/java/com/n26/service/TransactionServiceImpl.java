package com.n26.service;

import com.n26.dto.TransactionStatisticDto;
import com.n26.dto.TransactionUnitDto;
import com.n26.mapper.TransactionStatisticMapper;
import com.n26.mapper.TransactionUnitMapper;
import com.n26.model.CalculatedUnit;
import com.n26.model.TransactionStatistic;
import com.n26.model.TransactionUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.HALF_UP;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    public static final int TIME_TO_STORE_IN_SEC = 60;
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = HALF_UP;
    private final CircularFifoQueue<CalculatedUnit> queue;
    private final ReentrantLock lock;
    private TransactionUnitMapper transactionUnitMapper;
    private TransactionStatisticMapper transactionStatisticMapper;

    public TransactionServiceImpl(TransactionUnitMapper transactionUnitMapper,
                                  TransactionStatisticMapper transactionStatisticMapper) {
        this.queue = new CircularFifoQueue<>(TIME_TO_STORE_IN_SEC);
        for (int i = 0; i < TIME_TO_STORE_IN_SEC; i++) {
            this.queue.add(new CalculatedUnit(ZERO, ZERO, ZERO, 0L));
        }
        this.lock = new ReentrantLock();
        this.transactionUnitMapper = transactionUnitMapper;
        this.transactionStatisticMapper = transactionStatisticMapper;
    }

    public List<CalculatedUnit> getRawData() {
        lock.lock();
        try {
            return queue.stream().map(CalculatedUnit::new).collect(toList());
        } finally {
            lock.unlock();
        }
    }

    public List<CalculatedUnit> getNotEmptyRawData() {
        lock.lock();
        try {
            return queue.stream().filter(cu -> cu.getSum().compareTo(ZERO) != 0).map(CalculatedUnit::new)
                    .collect(toList());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public TransactionStatisticDto getStatistic() {
        BigDecimal overallSum = ZERO;
        BigDecimal overallMin = ZERO;
        BigDecimal overallMax = ZERO;
        long count = 0;
        List<CalculatedUnit> data;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            data = queue.stream().filter(cu -> cu.getSum().compareTo(ZERO) != 0).collect(toList());
        } finally {
            lock.unlock();
        }
        for (CalculatedUnit calculatedUnit : data) {
            overallSum = overallSum.add(calculatedUnit.getSum());
            final BigDecimal min = calculatedUnit.getMin();
            overallMin = overallMin.compareTo(ZERO) == 0 ? min : overallMin.min(min);
            overallMax = overallMax.max(calculatedUnit.getMax());
            count += calculatedUnit.getCount();
        }
        overallSum = overallSum.setScale(SCALE, ROUNDING_MODE);
        overallMax = overallMax.setScale(SCALE, ROUNDING_MODE);
        overallMin = overallMin.setScale(SCALE, ROUNDING_MODE);
        BigDecimal average;
        if (count == 0) {
            average = ZERO.setScale(SCALE, ROUNDING_MODE);
        } else {
            average = overallSum.divide(new BigDecimal(count), SCALE, ROUNDING_MODE);
        }

        final TransactionStatistic transactionStatistic = new TransactionStatistic(
                overallSum, overallMax, overallMin, average, count);

        return transactionStatisticMapper.toDto(transactionStatistic);
    }

    @Override
    public void create(TransactionUnitDto transactionUnitDto) {
        TransactionUnit transactionUnit = transactionUnitMapper.toEntity(transactionUnitDto);
        BigDecimal amount = transactionUnit.getAmount();
        LocalDateTime timestamp = transactionUnit.getTimestamp();
        LocalDateTime utcNow = LocalDateTime.now(Clock.systemUTC());
        final long secondInMinute = Duration.between(timestamp, utcNow).getSeconds();
        if (secondInMinute > 59 || secondInMinute < 0) {
            // TODO OPINTA: throw exception and test it
            log.error("Transaction is not in the last minute");
            return;
        }
        int index = queue.size() - (int)secondInMinute - 1;

        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            final CalculatedUnit calculatedUnit = queue.get(index);
            calculatedUnit.setSum(calculatedUnit.getSum().add(amount));
            calculatedUnit.setMax(calculatedUnit.getMax().max(amount));
            final BigDecimal min = calculatedUnit.getMin();
            calculatedUnit.setMin(min.compareTo(ZERO) == 0 ? amount : min.min(amount));
            calculatedUnit.setCount(calculatedUnit.getCount() + 1);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeAll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            queue.clear();
            for (int i = 0; i < TIME_TO_STORE_IN_SEC; i++) {
                queue.add(new CalculatedUnit(ZERO, ZERO, ZERO, 0L));
            }
        } finally {
            lock.unlock();
        }
    }

    @Scheduled(fixedRate = 1000)
    public void tic() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            queue.add(new CalculatedUnit(ZERO, ZERO, ZERO, 0L));
        } finally {
            lock.unlock();
        }
    }
}
