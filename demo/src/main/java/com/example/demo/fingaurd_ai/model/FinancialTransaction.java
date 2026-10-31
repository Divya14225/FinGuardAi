package com.example.demo.fingaurd_ai.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "financial_transactions")
@CompoundIndexes({
        @CompoundIndex(name = "idx_tx_user_ref", def = "{'user': 1, 'referenceCode': 1}")
})
public class FinancialTransaction {

    @Id
    private String id;

    @DBRef
    private AppUser user;

    private BigDecimal amount;
    private String currency;
    private String category;
    private String description;
    private String referenceCode;
    private LocalDateTime occurredAt;

    protected FinancialTransaction() {}

    public FinancialTransaction(AppUser user, BigDecimal amount, String currency, String category, String description, String referenceCode, LocalDateTime occurredAt) {
        this.user = user;
        this.amount = amount;
        this.currency = currency;
        this.category = category;
        this.description = description;
        this.referenceCode = referenceCode;
        this.occurredAt = occurredAt;
    }

    public String getId() { return id; }
    public AppUser getUser() { return user; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getReferenceCode() { return referenceCode; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}