package com.example.ecommerce_system.exception.product;

public class ProductRetrievalException extends RuntimeException {
    public ProductRetrievalException(String identifier) {
        super("Failed to retrieve '" + identifier + "'.");
    }
}
