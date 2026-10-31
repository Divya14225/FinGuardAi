package com.example.demo.fingaurd_ai.controller;


import com.example.demo.fingaurd_ai.dto.AdminRegisterRequest;
import com.example.demo.fingaurd_ai.service.AuthService;
import com.example.demo.fingaurd_ai.dto.AuthDtos;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDtos.AuthResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }
    @PostMapping("/admin/register")
    public ResponseEntity<AuthDtos.AuthResponse> registerAdmin(
            @Valid @RequestBody AdminRegisterRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registerAdmin(request));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
