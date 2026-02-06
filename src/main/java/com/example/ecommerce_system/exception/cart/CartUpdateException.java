package com.example.ecommerce_system.exception.cart;

public class CartUpdateException extends RuntimeException {
    public CartUpdateException(String cartItemId) {
        super("Failed to update cart item quantity: " + cartItemId);
    }
}
