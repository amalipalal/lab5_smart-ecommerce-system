package com.example.ecommerce_system.exception.cart;

public class CartRetrievalException extends RuntimeException {
    public CartRetrievalException(String identifier) {
        super("Failed to retrieve cart/cart items: " + identifier);
    }
}
