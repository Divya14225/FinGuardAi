package com.example.demo.fingaurd_ai.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public record RegisterRequest(
            @NotBlank(message = "Email cannot be blank")
            @Email(message = "Invalid email format")
            String email,

            @NotBlank(message = "Password cannot be blank")
            @Size(min = 6, message = "Password must be at least 6 characters long")
            String password
    ) {}

    public record LoginRequest(
            @NotBlank(message = "Email cannot be blank")
            @Email(message = "Invalid email format")
            String email,

            @NotBlank(message = "Password cannot be blank")
            String password
    ) {}

    public record AuthResponse(
            String token,
            String email,
            String role
    ) {}
}