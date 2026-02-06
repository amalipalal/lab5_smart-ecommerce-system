package com.example.ecommerce_system.util.handler;

import com.example.ecommerce_system.dto.ErrorResponseDto;
import com.example.ecommerce_system.exception.customer.CustomerCreationException;
import com.example.ecommerce_system.exception.customer.CustomerNotFoundException;
import com.example.ecommerce_system.exception.customer.CustomerRetrievalException;
import com.example.ecommerce_system.exception.customer.CustomerSearchException;
import com.example.ecommerce_system.exception.customer.CustomerUpdateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomerResponseHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleCustomerNotFound(CustomerNotFoundException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(CustomerCreationException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleCustomerCreation(CustomerCreationException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(CustomerUpdateException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleCustomerUpdate(CustomerUpdateException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(CustomerRetrievalException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleCustomerRetrieval(CustomerRetrievalException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(CustomerSearchException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleCustomerSearch(CustomerSearchException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleIllegalArgument(IllegalArgumentException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }
}
