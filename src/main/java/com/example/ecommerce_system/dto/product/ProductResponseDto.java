package com.example.ecommerce_system.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class ProductResponseDto {
    private UUID productId;
    private UUID categoryId;
    private String name;
    private String description;
    private double price;
    private int stock;
    private Instant updatedAt;
}
