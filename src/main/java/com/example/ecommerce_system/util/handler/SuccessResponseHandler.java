package com.example.ecommerce_system.util.handler;

import com.example.ecommerce_system.dto.SuccessResponseDto;
import org.springframework.http.HttpStatus;

public class SuccessResponseHandler {
    private SuccessResponseHandler(){}

    public static <T> SuccessResponseDto<T> generateSuccessResponse(HttpStatus status, T data) {
        return new SuccessResponseDto<>(status, "success", data);
    }
}
