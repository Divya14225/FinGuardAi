
package com.example.demo.fingaurd_ai.repository;

import com.example.demo.fingaurd_ai.model.FinancialTransaction;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository
        extends MongoRepository<FinancialTransaction, String> {

    // Find all transactions of a particular user
    List<FinancialTransaction> findByUser_Id(String userId);

    // Find transactions ordered by occurrence date
    List<FinancialTransaction> findByUser_IdOrderByOccurredAtDesc(String userId);

    // Check duplicate reference codes for a user
    boolean existsByUser_IdAndReferenceCode(
            String userId,
            String referenceCode
    );
}
