package com.example.ecommerce_system.exception.auth;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("User with email '" + email + "' already exists.");
    }
}
