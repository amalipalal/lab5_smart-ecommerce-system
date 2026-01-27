package com.example.ecommerce_system.exception.category;

public class CategoryCreationException extends RuntimeException {
    public CategoryCreationException(String identifier) {
        super("Failed to create category '" + identifier + "'.");
    }
}
