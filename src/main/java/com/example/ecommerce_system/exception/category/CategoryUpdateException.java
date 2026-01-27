package com.example.ecommerce_system.exception.category;

public class CategoryUpdateException extends RuntimeException {
    public CategoryUpdateException(String identifier) {
        super("Failed to update the category '" + identifier + "'.");
    }
}
