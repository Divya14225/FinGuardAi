package com.example.demo.fingaurd_ai.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "audit_reports")
public class AuditReport {

    @Id
    private String id;

    @DBRef
    private AppUser user;

    private String transactionId;

    private Instant time;

    private int flaggedCount;

    private double riskScore;

    private List<Finding> finding;

    private String aiExplanation;

    public record Finding(
            String operation,
            String rule,
            String message,
            int points
    ) {}

    protected AuditReport() {}

    public AuditReport(
            AppUser user,
            String transactionId,
            int flaggedCount,
            double riskScore,
            List<Finding> finding,
            String aiExplanation
    ) {
        this.user = user;
        this.transactionId = transactionId;
        this.time = Instant.now();
        this.flaggedCount = flaggedCount;
        this.riskScore = riskScore;
        this.finding = finding;
        this.aiExplanation = aiExplanation;
    }

    public String getId() {
        return id;
    }

    public AppUser getUser() {
        return user;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Instant getTime() {
        return time;
    }

    public int getFlaggedCount() {
        return flaggedCount;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public List<Finding> getFinding() {
        return finding;
    }

    public String getAiExplanation() {
        return aiExplanation;
    }
}
