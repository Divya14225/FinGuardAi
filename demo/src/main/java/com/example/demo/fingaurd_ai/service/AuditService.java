
package com.example.demo.fingaurd_ai.service;

import com.example.demo.fingaurd_ai.model.AppUser;
import com.example.demo.fingaurd_ai.model.AuditReport;
import com.example.demo.fingaurd_ai.model.AuditReport.Finding;
import com.example.demo.fingaurd_ai.model.FinancialTransaction;

import com.example.demo.fingaurd_ai.repository.AuditReportRepository;
import com.example.demo.fingaurd_ai.repository.UserRepository;
import com.example.demo.fingaurd_ai.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AuditService {

    private final UserRepository users;
    private final AuditReportRepository reports;
    private final FraudDetection fraud;
    private final AiService ai;
    private final TransactionRepository transactions;

    public AuditService(
            UserRepository users,
            AuditReportRepository reports,
            FraudDetection fraud,
            AiService ai,
            TransactionRepository transactions) {

        this.users = users;
        this.reports = reports;
        this.fraud = fraud;
        this.ai = ai;
        this.transactions = transactions;
    }

    // Response DTO
    public record AuditResponse(
            String reportId,
            String transactionId,
            int flaggedCount,
            double riskScore,
            List<Finding> finding,
            String aiExplanation
    ) {}

    // Analyze individual transaction
    public AuditResponse analyzeTransaction(
            String email,
            FinancialTransaction transaction,
            String operation) {

        AppUser user = users.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        // Validate transaction ownership
        if (transaction.getUser() == null ||
                !user.getId().equals(transaction.getUser().getId())) {

            throw new SecurityException(
                    "Transaction does not belong to this user");
        }

        // 1. Detect suspicious activity
        FraudDetection.FraudResult result =
                fraud.analyze(transaction, operation);

        // 2. Generate AI explanation
        String explanation = ai.explain(
                transaction,
                result.findings(),
                result.riskScore()
        );

        // 3. Save audit report
        AuditReport report = reports.save(
                new AuditReport(
                        user,
                        transaction.getId(),
                        result.flaggedCount(),
                        result.riskScore(),
                        result.findings(),
                        explanation
                )
        );

        // 4. Return response
        return new AuditResponse(
                report.getId(),
                report.getTransactionId(),
                report.getFlaggedCount(),
                report.getRiskScore(),
                report.getFinding(),
                report.getAiExplanation()
        );
    }

    // Manually audit all transactions belonging to a user
    public AuditResponse run(String email) {

        AppUser user = users.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        List<FinancialTransaction> userTransactions =
                transactions.findByUser_Id(user.getId());

        List<Finding> allFindings = new ArrayList<>();

        double totalRiskScore = 0;
        int flaggedCount = 0;
        int analyzedCount = 0;

        String lastReportId = null;
        String lastTransactionId = null;
        String lastExplanation = null;

        for (FinancialTransaction transaction : userTransactions) {

            AuditResponse response = analyzeTransaction(
                    email,
                    transaction,
                    "AUDIT"
            );

            allFindings.addAll(response.finding());

            totalRiskScore += response.riskScore();
            flaggedCount += response.flaggedCount();

            lastReportId = response.reportId();
            lastTransactionId = response.transactionId();
            lastExplanation = response.aiExplanation();

            analyzedCount++;
        }

        double averageRiskScore = analyzedCount == 0
                ? 0
                : totalRiskScore / analyzedCount;

        return new AuditResponse(
                lastReportId,
                lastTransactionId,
                flaggedCount,
                averageRiskScore,
                allFindings,
                lastExplanation == null
                        ? "No transactions available for auditing."
                        : lastExplanation
        );
    }

    // USER: Get all audit reports belonging to the logged-in user
    public List<AuditReport> list(String email) {

        AppUser user = users.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return reports.findByUser_IdOrderByTimeDesc(user.getId());
    }

    // USER: Get a particular audit report belonging to the logged-in user
    public AuditReport get(String email, String id) {

        AppUser user = users.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return reports.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() ->
                        new NoSuchElementException("Audit report not found"));
    }

    // ADMIN: Get all audit reports belonging to all users
    public List<AuditReport> listAllAudits() {

        return reports.findAll()
                .stream()
                .sorted((a, b) -> b.getTime().compareTo(a.getTime()))
                .toList();
    }

    // ADMIN: Get any audit report by ID
    public AuditReport getAnyAudit(String id) {

        return reports.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Audit report not found"));
    }
}
