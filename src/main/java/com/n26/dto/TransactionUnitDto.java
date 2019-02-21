package com.n26.dto;

import com.n26.validator.CheckTransaction;
import lombok.Data;

@Data
@CheckTransaction
public class TransactionUnitDto {
    private String amount;
    private String timestamp;
}
