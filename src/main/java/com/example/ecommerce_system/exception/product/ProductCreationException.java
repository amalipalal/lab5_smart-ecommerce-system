package com.example.ecommerce_system.exception.product;

public class ProductCreationException extends RuntimeException {
    public ProductCreationException(String identifier) {
        super("Failed to create product '" + identifier + "'.");
    }
}
