package com.example.ecommerce_system.util;

import com.example.ecommerce_system.dto.ErrorResponseDto;
import com.example.ecommerce_system.exception.category.CategoryNotFoundException;
import com.example.ecommerce_system.exception.category.DuplicateCategoryException;
import com.example.ecommerce_system.exception.category.CategoryDeletionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CategoryResponseHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleCategoryNotFound(CategoryNotFoundException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(DuplicateCategoryException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleDuplicateCategoryName(DuplicateCategoryException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(CategoryDeletionException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleCategoryDeletion(CategoryDeletionException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }

}
