package com.example.ecommerce_system.exception.customer;

public class CustomerSearchException extends RuntimeException {
    public CustomerSearchException(String query) {
        super("Failed to search customers with query: " + query);
    }
}
