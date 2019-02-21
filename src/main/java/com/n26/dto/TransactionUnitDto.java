package com.n26.dto;

import com.n26.validator.CheckTransaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@CheckTransaction
public class TransactionUnitDto {
    private String amount;
    private String timestamp;
}
