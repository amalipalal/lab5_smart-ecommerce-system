package com.example.ecommerce_system.exception.review;

public class ReviewRetrievalException extends RuntimeException {
    public ReviewRetrievalException(String identifier) {
        super("Failed to retrieve review '" + identifier);
    }
}
