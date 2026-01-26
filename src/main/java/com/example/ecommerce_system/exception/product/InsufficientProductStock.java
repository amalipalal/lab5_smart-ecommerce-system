package com.example.ecommerce_system.exception.product;

public class InsufficientProductStock extends RuntimeException {
    public InsufficientProductStock(String productId) {
        super("Insufficient stock of product '" + productId + "'.");
    }
}

