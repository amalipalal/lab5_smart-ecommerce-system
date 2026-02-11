package com.example.ecommerce_system.exception.order;

public class OrderCannotBeCancelledException extends RuntimeException {
    public OrderCannotBeCancelledException(String orderId) {
        super("Order '" + orderId + "' cannot be cancelled. Only pending orders can be cancelled.");
    }
}
