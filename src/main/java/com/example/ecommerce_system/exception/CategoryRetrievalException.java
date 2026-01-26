package com.example.ecommerce_system.exception;

public class CategoryRetrievalException extends RuntimeException {
    public CategoryRetrievalException(String identifier) {
        super("Failed to retrieve '" + identifier + "'.");
    }
}
