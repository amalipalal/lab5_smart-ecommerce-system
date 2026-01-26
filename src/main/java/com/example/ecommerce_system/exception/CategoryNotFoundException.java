package com.example.ecommerce_system.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String identifier) {
        super("The Category '" + identifier + "' was not found.");
    }
}
