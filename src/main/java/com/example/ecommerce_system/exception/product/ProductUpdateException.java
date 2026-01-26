package com.example.ecommerce_system.exception.product;

public class ProductUpdateException extends RuntimeException {
    public ProductUpdateException(String identifier) {
        super("Failed to update the product '" + identifier + "'.");
    }
}
