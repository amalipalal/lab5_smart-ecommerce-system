package com.example.ecommerce_system.util.handler;

import com.example.ecommerce_system.dto.ErrorResponseDto;
import com.example.ecommerce_system.exception.auth.DuplicateEmailException;
import com.example.ecommerce_system.exception.auth.InvalidCredentialsException;
import com.example.ecommerce_system.exception.auth.UserNotFoundException;
import com.example.ecommerce_system.exception.auth.WeakPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthResponseHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleDuplicateEmail(DuplicateEmailException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleInvalidCredentials(InvalidCredentialsException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleUserNotFound(UserNotFoundException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }

    @ExceptionHandler(WeakPasswordException.class)
    public ResponseEntity<ErrorResponseDto<String>> handleWeakPassword(WeakPasswordException exception) {
        return ErrorResponseHandler.generateErrorMessage(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                exception.getClass().getSimpleName());
    }
}
