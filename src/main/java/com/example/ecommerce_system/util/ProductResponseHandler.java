package com.example.ecommerce_system.util;

import com.example.ecommerce_system.dto.ErrorResponseDto;
import com.example.ecommerce_system.exception.product.DeleteProductException;
import com.example.ecommerce_system.exception.product.InsufficientProductStock;
import com.example.ecommerce_system.exception.product.ProductCreationException;
import com.example.ecommerce_system.exception.product.ProductNotFoundException;
import com.example.ecommerce_system.exception.product.ProductRetrievalException;
import com.example.ecommerce_system.exception.product.ProductSearchException;
import com.example.ecommerce_system.exception.product.ProductUpdateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProductResponseHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleProductNotFound(ProductNotFoundException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(ProductCreationException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleProductCreation(ProductCreationException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(ProductUpdateException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleProductUpdate(ProductUpdateException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(DeleteProductException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleProductDeletion(DeleteProductException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(InsufficientProductStock.class)
    public ResponseEntity<ErrorResponseDto<String>> handleInsufficientStock(InsufficientProductStock exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(ProductRetrievalException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleProductRetrieval(ProductRetrievalException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(ProductSearchException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleProductSearch(ProductSearchException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }
}
