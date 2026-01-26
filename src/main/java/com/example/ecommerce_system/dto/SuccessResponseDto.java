package com.example.ecommerce_system.dto;

import org.springframework.http.HttpStatus;

public record SuccessResponseDto<T>(HttpStatus status, String message, T data){}
