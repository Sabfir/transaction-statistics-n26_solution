package com.n26.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TransactionUnit {
    private BigDecimal amount;
    private LocalDateTime timestamp;
}
