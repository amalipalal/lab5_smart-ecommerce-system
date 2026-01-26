package com.example.ecommerce_system.dto;

import org.springframework.http.HttpStatus;

public record ErrorResponseDto<T> (HttpStatus status, String message, T error){}
