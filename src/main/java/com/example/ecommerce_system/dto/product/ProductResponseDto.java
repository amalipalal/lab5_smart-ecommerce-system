package com.example.ecommerce_system.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Data
@SuperBuilder
public class ProductResponseDto {
    private UUID productId;
    private UUID categoryId;
    private String name;
    private String description;
    private double price;
    private int stock;
    private Instant updatedAt;
}
