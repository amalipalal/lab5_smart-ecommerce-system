package com.example.ecommerce_system.exception.cart;

public class CartItemRemoveException extends RuntimeException {
    public CartItemRemoveException(String cartItemId) {
        super("Failed to remove cart item: " + cartItemId);
    }
}
