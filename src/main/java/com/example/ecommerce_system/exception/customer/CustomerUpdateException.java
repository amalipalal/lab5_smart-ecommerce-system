package com.example.ecommerce_system.exception.customer;

public class CustomerUpdateException extends RuntimeException {
    public CustomerUpdateException(String identifier) {
        super("Failed to update customer '" + identifier + "'.");
    }
}