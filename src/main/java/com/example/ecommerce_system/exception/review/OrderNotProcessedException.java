package com.example.ecommerce_system.exception.review;

public class OrderNotProcessedException extends RuntimeException {
    public OrderNotProcessedException(String orderId) {
        super(String.format("Order %s has not been processed yet. Only processed orders can be reviewed.", orderId));
    }
}
