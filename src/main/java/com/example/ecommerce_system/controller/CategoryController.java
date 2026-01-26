package com.example.ecommerce_system.controller;

import com.example.ecommerce_system.dto.CategoryResponseDto;
import com.example.ecommerce_system.dto.SuccessResponseDto;
import com.example.ecommerce_system.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary="Retrieve all categories")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All categories retrieved"),
    })
    @GetMapping
    public SuccessResponseDto<List<CategoryResponseDto>> getCategories(
            @RequestParam @Min(1) @Max(100) int limit,
            @RequestParam @Min(0) int offset)
    {
        List<CategoryResponseDto> categories = categoryService.getAllCategories(limit, offset);
        return new SuccessResponseDto<>(HttpStatus.OK, "Success", categories);
    }
}
