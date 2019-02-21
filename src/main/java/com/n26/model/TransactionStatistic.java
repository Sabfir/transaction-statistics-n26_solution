package com.n26.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatistic {
    private BigDecimal sum;
    private BigDecimal max;
    private BigDecimal min;
    private BigDecimal average;
    private long count;
}
