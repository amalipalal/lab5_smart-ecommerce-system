package com.example.ecommerce_system.exception.customer;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String identifier) {
        super("Customer with identifier '" + identifier + "' not found.");
    }
}
