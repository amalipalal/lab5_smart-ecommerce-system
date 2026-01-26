package com.example.ecommerce_system.exception;

public class DatabaseConnectionException extends RuntimeException {
    public DatabaseConnectionException(Throwable cause) {
        super(cause);
    }
}
