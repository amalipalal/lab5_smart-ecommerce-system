package com.example.ecommerce_system.exception.user;

public class UserCreationException extends RuntimeException {
    public UserCreationException(String identifier) {
        super("Failed to create user '" + identifier + "'.");
    }
}
