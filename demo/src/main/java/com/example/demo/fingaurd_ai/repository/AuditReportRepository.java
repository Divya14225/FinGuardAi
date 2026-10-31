package com.example.demo.fingaurd_ai.repository;


import com.example.demo.fingaurd_ai.model.AuditReport;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AuditReportRepository
        extends MongoRepository<AuditReport, String> {

    List<AuditReport> findByUser_IdOrderByTimeDesc(String userId);

    Optional<AuditReport> findByIdAndUser_Id(
            String id,
            String userId
    );

    List<AuditReport> findByTransactionIdOrderByTimeDesc(
            String transactionId
    );
}
