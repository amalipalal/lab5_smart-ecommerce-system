package com.example.ecommerce_system.exception.product;

public class DeleteProductException extends RuntimeException {
    public DeleteProductException(String identifier) {
        super("Failed to delete the product '" + identifier + "'.");
    }
}
