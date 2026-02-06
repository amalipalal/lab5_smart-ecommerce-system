package com.example.ecommerce_system.exception.cart;

public class CartItemAuthorizationException extends RuntimeException {
    public CartItemAuthorizationException(String identifier) {
        super("Cart item does not belong to customer: " + identifier);
    }
}
