package com.example.ecommerce_system.exception.customer;

public class CustomerRetrievalException extends RuntimeException {
    public CustomerRetrievalException(String identifier) {
        super("Failed to retrieve customer '" + identifier + "'.");
    }
}
