package com.example.ecommerce_system.exception.cart;

public class CartItemAddException extends RuntimeException {
    public CartItemAddException(String cartItemId) {
        super("Failed to add item to cart: " + cartItemId);
    }
}
