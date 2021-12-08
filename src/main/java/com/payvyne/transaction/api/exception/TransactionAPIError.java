package com.payvyne.transaction.api.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionAPIError {
    private String message;
    private int code;
}
