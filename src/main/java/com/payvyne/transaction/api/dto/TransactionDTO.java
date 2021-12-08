package com.payvyne.transaction.api.dto;

import com.payvyne.transaction.api.model.enums.Currency;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransactionDTO {
    private BigDecimal amount;
    private Currency currency;
}
