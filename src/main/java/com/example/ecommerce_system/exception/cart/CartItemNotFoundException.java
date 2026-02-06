package com.example.ecommerce_system.exception.cart;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(String cartItemId) {
        super("Cart item not found: " + cartItemId);
    }
}
