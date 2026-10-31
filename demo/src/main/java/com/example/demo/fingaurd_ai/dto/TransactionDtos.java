package com.example.demo.fingaurd_ai.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDtos {

    public record CreateTransactionRequest(
            BigDecimal amount,
            String currency,
            String category,
            String description,
            String referenceCode,
            LocalDateTime occurredAt
    ) {}

    public record TransactionResponse(
            String id,
            BigDecimal amount,
            String currency,
            String category,
            String description,
            String referenceCode,
            LocalDateTime occurredAt
    ) {}
}
