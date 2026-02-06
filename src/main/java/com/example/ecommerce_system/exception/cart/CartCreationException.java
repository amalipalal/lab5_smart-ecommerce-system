package com.example.ecommerce_system.exception.cart;

public class CartCreationException extends RuntimeException {
    public CartCreationException(String cartId) {
        super("Failed to create cart: " + cartId);
    }
}
