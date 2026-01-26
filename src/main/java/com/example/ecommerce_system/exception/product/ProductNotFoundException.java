package com.example.ecommerce_system.exception.product;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String identifier) {
        super("Product '" + identifier + "' was not found.");
    }
}