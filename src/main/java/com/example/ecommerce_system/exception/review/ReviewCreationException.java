package com.example.ecommerce_system.exception.review;

public class ReviewCreationException extends RuntimeException {
    public ReviewCreationException(String identifier) {
        super("Failed to create review '" + identifier);
    }
}
