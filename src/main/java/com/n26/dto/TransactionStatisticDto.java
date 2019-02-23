package com.n26.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatisticDto {
    private String sum;
    private String avg;
    private String max;
    private String min;
    private long count;
}
