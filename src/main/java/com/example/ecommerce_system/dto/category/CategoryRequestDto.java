package com.example.ecommerce_system.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CategoryRequestDto {

    @NotBlank(
            groups = CreateCategoryRequest.class,
            message = "name cannot be empty"
    )
    private String name;

    @NotBlank(
            groups = CreateCategoryRequest.class,
            message = "description cannot be empty"
    )
    private String description;
}
