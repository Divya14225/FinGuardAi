package com.example.demo.fingaurd_ai.dto;

public record AdminRegisterRequest(
        String email,
        String password,
        String registrationKey
) {}