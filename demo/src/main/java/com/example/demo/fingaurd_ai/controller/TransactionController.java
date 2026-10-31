package com.example.demo.fingaurd_ai.controller;


import com.example.demo.fingaurd_ai.dto.TransactionDtos;
import com.example.demo.fingaurd_ai.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/add")
    public ResponseEntity<TransactionDtos.TransactionResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionDtos.CreateTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.create(userDetails.getUsername(), request));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TransactionDtos.TransactionResponse>> list(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transactionService.list(userDetails.getUsername()));
    }
}
