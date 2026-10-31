package com.example.demo.fingaurd_ai.controller;


import com.example.demo.fingaurd_ai.service.AuditService;
import com.example.demo.fingaurd_ai.model.AuditReport;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audits")
public class AuditController {
    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping("/run")
    public ResponseEntity<AuditService.AuditResponse> runAudit(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(auditService.run(userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<AuditReport>> listAudits(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(auditService.list(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditReport> getAudit(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String id) { // ID is now String (MongoDB ObjectId)
        return ResponseEntity.ok(auditService.get(userDetails.getUsername(), id));
    }
}
