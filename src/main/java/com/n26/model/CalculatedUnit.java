package com.n26.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculatedUnit {
    private BigDecimal sum;
    private BigDecimal max;
    private BigDecimal min;
    private long count;

    public CalculatedUnit(CalculatedUnit calculatedUnit) {
        this.sum = calculatedUnit.getSum();
        this.max = calculatedUnit.getMax();
        this.min = calculatedUnit.getMin();
        this.count = calculatedUnit.getCount();
    }
}
