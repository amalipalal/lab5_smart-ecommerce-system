package com.example.ecommerce_system.exception.user;

public class UserRetrievalException extends RuntimeException {
    public UserRetrievalException(String identifier) {
        super("Failed to retrieve user '" + identifier + "'.");
    }
}
