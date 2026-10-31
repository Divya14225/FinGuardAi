package com.example.demo.fingaurd_ai.service;

import com.example.demo.fingaurd_ai.model.AuditReport.Finding;


import com.example.demo.fingaurd_ai.model.FinancialTransaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class FraudDetection {

    public record FraudResult(
            int flaggedCount,
            double riskScore,
            List<Finding> findings
    ) {}

    public FraudResult analyze(
            FinancialTransaction transaction,
            String operation
    ) {

        List<Finding> findings = new ArrayList<>();

        BigDecimal amount = transaction.getAmount();

        // Rule 1: Invalid amount
        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            findings.add(new Finding(
                    operation,
                    "INVALID_AMOUNT",
                    "Transaction amount must be positive.",
                    40
            ));
        }

        // Rule 2: High-value transaction
        else if (amount.compareTo(
                new BigDecimal("50000")) >= 0) {

            findings.add(new Finding(
                    operation,
                    "HIGH_AMOUNT",
                    "Transaction amount is at least 50,000. Verify supporting documents.",
                    35
            ));
        }

        // Rule 3: Missing reference code
        if (transaction.getReferenceCode() == null ||
                transaction.getReferenceCode().isBlank()) {

            findings.add(new Finding(
                    operation,
                    "MISSING_REFERENCE",
                    "Transaction reference code is missing.",
                    20
            ));
        }

        int score = findings.stream()
                .mapToInt(Finding::points)
                .sum();

        score = Math.min(score, 100);

        return new FraudResult(
                findings.size(),
                score,
                findings
        );
    }
}
