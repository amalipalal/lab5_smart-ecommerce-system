package com.example.ecommerce_system.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class ProductRequestDto {
    @NotBlank(
            groups = CreateProductRequest.class,
            message = "name cannot be empty"
    )
    private String name;

    @NotBlank(
            groups = CreateProductRequest.class,
            message = "description cannot be empty"
    )
    private String description;

    @NotNull(
            groups = CreateProductRequest.class,
            message = "price cannot be empty"
    )
    @PositiveOrZero(
            groups = {CreateProductRequest.class, UpdateProductRequest.class},
            message = "price cannot be less than 0"
    )
    Double price;

    @NotNull(
            groups = CreateProductRequest.class,
            message = "stock cannot be empty"
    )
    @PositiveOrZero(
            groups = {CreateProductRequest.class, UpdateProductRequest.class},
            message = "stock cannot be less than 0"
    )
    Integer stock;

    @NotNull(
            groups = {CreateProductRequest.class},
            message = "categoryId cannot be empty"
    )
    UUID categoryId;
}
