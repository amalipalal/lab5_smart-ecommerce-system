package com.example.ecommerce_system.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.ecommerce_system.dto.auth.AuthResponseDto;
import com.example.ecommerce_system.dto.auth.LoginRequestDto;
import com.example.ecommerce_system.dto.auth.SignupRequestDto;
import com.example.ecommerce_system.exception.auth.DuplicateEmailException;
import com.example.ecommerce_system.exception.auth.InvalidCredentialsException;
import com.example.ecommerce_system.exception.auth.UserNotFoundException;
import com.example.ecommerce_system.exception.auth.WeakPasswordException;
import com.example.ecommerce_system.model.Customer;
import com.example.ecommerce_system.model.Role;
import com.example.ecommerce_system.model.User;
import com.example.ecommerce_system.store.UserStore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserStore userStore;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${jwt.token.secret-key}")
    private String secretKey;

    /**
     * Register a new user with the provided credentials.
     * Validates password strength, checks for duplicate email, hashes the password, and persists the user.
     * Also creates a customer record for the new user.
     */
    public AuthResponseDto signup(SignupRequestDto request) {
        validatePassword(request.getPassword());

        Optional<User> existingUser = userStore.getUserByEmail(request.getEmail());
        if (existingUser.isPresent())
            throw new DuplicateEmailException(request.getEmail());

        var newUser = createUser(request);
        var newCustomer = createCustomer(request);

        var createdUser = userStore.createUser(newUser, newCustomer);

        return mapToAuthResponse(createdUser, null);
    }

    private String generateJwtToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        return JWT.create()
                .withSubject(user.getUserId().toString())
                .withClaim("role", user.getRole().name())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 86400000))
                .sign(algorithm);
    }

    private User createUser(SignupRequestDto request) {
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        return User.builder()
                .userId(UUID.randomUUID())
                .email(request.getEmail())
                .passwordHash(hashedPassword)
                .role(Role.CUSTOMER)
                .createdAt(Instant.now())
                .build();
    }

    private Customer createCustomer(SignupRequestDto request) {
        return Customer.builder()
                .customerId(UUID.randomUUID())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .isActive(true)
                .createdAt(Instant.now())
                .build();
    }

    /**
     * Authenticate a user with email and password.
     * Verifies credentials and returns user details if valid.
     */
    public AuthResponseDto login(LoginRequestDto request) {
        User user = userStore.getUserByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = generateJwtToken(user);
        return mapToAuthResponse(user, token);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new WeakPasswordException("Password must be at least 8 characters long.");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new WeakPasswordException("Password must contain at least one uppercase letter.");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new WeakPasswordException("Password must contain at least one lowercase letter.");
        }

        if (!password.matches(".*\\d.*")) {
            throw new WeakPasswordException("Password must contain at least one digit.");
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new WeakPasswordException("Password must contain at least one special character.");
        }
    }

    private AuthResponseDto mapToAuthResponse(User user, String token) {
        return AuthResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .token(token)
                .build();
    }
}
