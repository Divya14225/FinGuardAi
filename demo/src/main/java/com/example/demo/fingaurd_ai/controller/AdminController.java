
package com.example.demo.fingaurd_ai.controller;

import com.example.demo.fingaurd_ai.dto.TransactionDtos;
import com.example.demo.fingaurd_ai.model.AuditReport;
import com.example.demo.fingaurd_ai.service.AuditService;
import com.example.demo.fingaurd_ai.service.TransactionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final TransactionService transactionService;
    private final AuditService auditService;

    public AdminController(
            TransactionService transactionService,
            AuditService auditService) {

        this.transactionService = transactionService;
        this.auditService = auditService;
    }

    // ================= TRANSACTIONS =================

    // Admin can view all users' transactions
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDtos.TransactionResponse>>
    getAllTransactions() {

        return ResponseEntity.ok(
                transactionService.listAllTransactions()
        );
    }

    // ================= AUDITS =================

    // Admin can view all users' audit reports
    @GetMapping("/audits")
    public ResponseEntity<List<AuditReport>> getAllAudits() {

        return ResponseEntity.ok(
                auditService.listAllAudits()
        );
    }

    // Admin can view a particular audit report
    @GetMapping("/audits/{id}")
    public ResponseEntity<AuditReport> getAuditById(
            @PathVariable String id) {

        return ResponseEntity.ok(
                auditService.getAnyAudit(id)
        );
    }
}
