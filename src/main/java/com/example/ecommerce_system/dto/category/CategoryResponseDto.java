package com.example.ecommerce_system.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@SuperBuilder
@Data
public class CategoryResponseDto {
    private final UUID categoryId;
    private final String name;
    private final String description;
    private final Instant createdAt;
    private final Instant updatedAt;
}
