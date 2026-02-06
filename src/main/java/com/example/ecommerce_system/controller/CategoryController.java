package com.example.ecommerce_system.controller;

import com.example.ecommerce_system.dto.*;
import com.example.ecommerce_system.dto.category.CategoryRequestDto;
import com.example.ecommerce_system.dto.category.CategoryResponseDto;
import com.example.ecommerce_system.dto.category.CreateCategoryRequest;
import com.example.ecommerce_system.dto.category.UpdateCategoryRequest;
import com.example.ecommerce_system.service.CategoryService;
import com.example.ecommerce_system.util.handler.SuccessResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public SuccessResponseDto<List<CategoryResponseDto>> getAllCategories(
            @RequestParam @Min(1) @Max(100) int limit,
            @RequestParam @Min(0) int offset)
    {
        List<CategoryResponseDto> categories = categoryService.getAllCategories(limit, offset);
        return SuccessResponseHandler.generateSuccessResponse(HttpStatus.OK, categories);
    }

    @Operation(summary = "Retrieve a single category by categoryId")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A single category retrieved")
    })
    @GetMapping("/{id}")
    public SuccessResponseDto<CategoryResponseDto> getCategoryById(@PathVariable UUID id) {
        CategoryResponseDto category = categoryService.getCategory(id);
        return SuccessResponseHandler.generateSuccessResponse(HttpStatus.OK, category);
    }

    @Operation(summary = "Retrieve categories with name containing query")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Find all categories with names containing the query")
    })
    @GetMapping("/search")
    public SuccessResponseDto<List<CategoryResponseDto>> searchCategoriesByName(
            @RequestParam @NotBlank String query,
            @RequestParam @Min(1) @Max(100) int limit,
            @RequestParam @Min(0) int offset
    ) {
        List<CategoryResponseDto> categories = categoryService.getCategories(query, limit, offset);
        return SuccessResponseHandler.generateSuccessResponse(HttpStatus.OK, categories);
    }

    @PostMapping
    public SuccessResponseDto<CategoryResponseDto> addCategory(
            @RequestBody @Validated(CreateCategoryRequest.class) CategoryRequestDto category
    ) {
        CategoryResponseDto categoryCreated = categoryService.createCategory(category);
        return SuccessResponseHandler.generateSuccessResponse(HttpStatus.CREATED, categoryCreated);
    }

    @PatchMapping("/{id}")
    public SuccessResponseDto<CategoryResponseDto> updateCategory(
            @PathVariable UUID id,
            @RequestBody @Validated(UpdateCategoryRequest.class) CategoryRequestDto update
    ) {
        CategoryResponseDto updatedCategory = categoryService.updateCategory(id, update);
        return SuccessResponseHandler.generateSuccessResponse(HttpStatus.OK, updatedCategory);
    }

    @Operation(summary = "Delete a category by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Category has associated products and cannot be deleted")
    })
    @DeleteMapping("/{id}")
    public SuccessResponseDto<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return SuccessResponseHandler.generateSuccessResponse(HttpStatus.NO_CONTENT, null);
    }
}
