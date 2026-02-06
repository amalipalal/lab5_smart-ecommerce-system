package com.example.ecommerce_system.exception.review;

public class CustomerHasNotOrderedProductException extends RuntimeException {
    public CustomerHasNotOrderedProductException(String customerId, String productId) {
        super(String.format("Customer %s has not ordered product %s", customerId, productId));
    }
}
