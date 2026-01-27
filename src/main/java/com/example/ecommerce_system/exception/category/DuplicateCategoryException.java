package com.example.ecommerce_system.exception.category;

public class DuplicateCategoryException extends RuntimeException {
    public DuplicateCategoryException(String name) {
        super("Category with name '" + name.toLowerCase() + "' already exists");
    }
}
