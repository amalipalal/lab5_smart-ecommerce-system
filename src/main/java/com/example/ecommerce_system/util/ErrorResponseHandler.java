package com.example.ecommerce_system.util;

import com.example.ecommerce_system.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorResponseHandler {
    private ErrorResponseHandler(){}

    public static <T> ResponseEntity<ErrorResponseDto<T>> generateErrorMessage(
            HttpStatus status, String message, T error
    ) {
        return new ResponseEntity<>(new ErrorResponseDto<>(status, message, error), status);
    }
}
