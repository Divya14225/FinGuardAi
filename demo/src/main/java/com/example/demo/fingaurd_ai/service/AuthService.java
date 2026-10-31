
package com.example.demo.fingaurd_ai.service;

import com.example.demo.fingaurd_ai.dto.AdminRegisterRequest;
import com.example.demo.fingaurd_ai.dto.AuthDtos;
import com.example.demo.fingaurd_ai.model.AppUser;
import com.example.demo.fingaurd_ai.repository.UserRepository;
import com.example.demo.fingaurd_ai.security.JwtService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${app.admin.registration-key}")
    private String adminRegistrationKey;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // Normal user registration
    public AuthDtos.AuthResponse register(
            AuthDtos.RegisterRequest request) {

        String email = request.email().trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        AppUser user = new AppUser(
                email,
                passwordEncoder.encode(request.password())
        );

        userRepository.save(user);

        String token = jwtService.generateToken(email);

        return new AuthDtos.AuthResponse(
                token,
                email,
                user.getRole()
        );
    }

    // Admin registration
    public AuthDtos.AuthResponse registerAdmin(
            AdminRegisterRequest request) {
        System.out.println("ADMIN REGISTRATION METHOD CALLED");
        System.out.println("Registration key configured: "
                + (adminRegistrationKey != null));

        if (request.registrationKey() == null ||
                !request.registrationKey().equals(adminRegistrationKey)) {

            throw new SecurityException(
                    "Invalid admin registration key");
        }

        String email = request.email().trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        AppUser user = new AppUser(
                email,
                passwordEncoder.encode(request.password()),
                "ADMIN"
        );

        userRepository.save(user);

        String token = jwtService.generateToken(email);

        return new AuthDtos.AuthResponse(
                token,
                email,
                user.getRole()
        );
    }

    // Login for both USER and ADMIN
    public AuthDtos.AuthResponse login(
            AuthDtos.LoginRequest request) {

        String email = request.email().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.password()
                )
        );

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password"));

        String token = jwtService.generateToken(email);

        return new AuthDtos.AuthResponse(
                token,
                email,
                user.getRole()
        );
    }
}
