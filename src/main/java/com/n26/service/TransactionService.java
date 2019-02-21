package com.n26.service;

import com.n26.dto.TransactionStatisticDto;
import com.n26.dto.TransactionUnitDto;

public interface TransactionService {

    TransactionStatisticDto getStatistic();

    void create(TransactionUnitDto transactionUnitDto);

    void removeAll();
}
