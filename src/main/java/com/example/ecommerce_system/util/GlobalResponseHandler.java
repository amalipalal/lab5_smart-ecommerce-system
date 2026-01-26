package com.example.ecommerce_system.util;

import com.example.ecommerce_system.dto.ErrorResponseDto;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalResponseHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {
                    errors.put(error.getField(), error.getDefaultMessage());
                });

        return ErrorResponseHandler.generateErrorMessage(HttpStatus.BAD_REQUEST, "validation failed", errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto<Map<String, String>>> handleConstraintErrors(
            ConstraintViolationException exception
    ) {
        String message = exception.getMessage();

        return ErrorResponseHandler.generateErrorMessage(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception
    ) {
        String message = String.format("HTTP method '%s' is not supported for this endpoint. Supported methods: %s",
                exception.getMethod(),
                exception.getSupportedHttpMethods());

        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.METHOD_NOT_ALLOWED,
                message,
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException exception
    ) {
        String message = String.format("Media type '%s' is not supported. Supported media types: %s",
                exception.getContentType(),
                exception.getSupportedMediaTypes());

        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                message,
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleMissingParams(
            MissingServletRequestParameterException exception
    ) {
        String message = String.format("Required parameter '%s' of type '%s' is missing",
                exception.getParameterName(),
                exception.getParameterType());

        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.BAD_REQUEST,
                message,
                exception.getClass().getSimpleName());
    }
}
