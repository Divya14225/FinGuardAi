package com.example.demo.fingaurd_ai.service;

import com.example.demo.fingaurd_ai.dto.TransactionDtos;
import com.example.demo.fingaurd_ai.model.AppUser;
import com.example.demo.fingaurd_ai.model.FinancialTransaction;
import com.example.demo.fingaurd_ai.repository.TransactionRepository;
import com.example.demo.fingaurd_ai.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactions;
    private final UserRepository users;

    public TransactionService(TransactionRepository transactions, UserRepository users) {
        this.transactions = transactions;
        this.users = users;
    }

    public TransactionDtos.TransactionResponse create(String email, TransactionDtos.CreateTransactionRequest req) {
        AppUser user = users.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        String ref = req.referenceCode().trim();

        if (transactions.existsByUser_IdAndReferenceCode(user.getId(), ref)) {
            throw new IllegalArgumentException("Reference code already exists for this user");
        }

        FinancialTransaction t = transactions.save(new FinancialTransaction(
                user,
                req.amount(),
                req.currency().toUpperCase(),
                req.category().trim(),
                req.description().trim(),
                ref,
                req.occurredAt()
        ));
        return toResponse(t);
    }

    public List<TransactionDtos.TransactionResponse> list(String email) {
        AppUser user = users.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return transactions.findByUser_IdOrderByOccurredAtDesc(user.getId())
                .stream()
                .map(TransactionService::toResponse)
                .toList();
    }
    public List<TransactionDtos.TransactionResponse> listAllTransactions() {

        return transactions.findAll()
                .stream()
                .map(TransactionService::toResponse)
                .toList();
    }
    public static TransactionDtos.TransactionResponse toResponse(FinancialTransaction t) {
        return new TransactionDtos.TransactionResponse(
                t.getId(),
                t.getAmount(),
                t.getCurrency(),
                t.getCategory(),
                t.getDescription(),
                t.getReferenceCode(),
                t.getOccurredAt()
        );
    }
}