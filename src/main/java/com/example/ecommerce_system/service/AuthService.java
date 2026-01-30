package com.example.ecommerce_system.service;

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
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserStore userStore;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Register a new user with the provided credentials.
     * Validates password strength, checks for duplicate email,
     * hashes the password, and persists the user.
     * Also creates a customer record for the new user.
     *
     * @param request signup request containing email, password, firstName, and lastName
     * @return {@link AuthResponseDto} containing user details
     * @throws WeakPasswordException if password doesn't meet requirements
     * @throws DuplicateEmailException if email is already registered
     */
    public AuthResponseDto signup(SignupRequestDto request) {
        validatePassword(request.getPassword());

        Optional<User> existingUser = userStore.getUserByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new DuplicateEmailException(request.getEmail());
        }

        var newUser = createUser(request);
        var newCustomer = createCustomer(request);

        var createdUser = userStore.createUser(newUser, newCustomer);
        return mapToAuthResponse(createdUser);
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
     *
     * @param request login request containing email and password
     * @return {@link AuthResponseDto} containing user details
     * @throws UserNotFoundException if user with email doesn't exist
     * @throws InvalidCredentialsException if password is incorrect
     */
    public AuthResponseDto login(LoginRequestDto request) {
        User user = userStore.getUserByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return mapToAuthResponse(user);
    }

    /**
     * Validate password strength.
     * Password must be at least 8 characters long and contain
     * at least one uppercase letter, one lowercase letter, one digit,
     * and one special character.
     *
     * @param password the password to validate
     * @throws WeakPasswordException if password doesn't meet requirements
     */
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

    /**
     * Map a User entity to AuthResponseDto.
     *
     * @param user the user entity
     * @return {@link AuthResponseDto}
     */
    private AuthResponseDto mapToAuthResponse(User user) {
        return AuthResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
