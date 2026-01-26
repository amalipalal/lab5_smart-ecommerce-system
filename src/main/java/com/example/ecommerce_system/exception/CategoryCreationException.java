package com.example.ecommerce_system.exception;

public class CategoryCreationException extends RuntimeException {
    public CategoryCreationException(String identifier) {
        super("Failed to create category '" + identifier + "'.");
    }
}
