package com.example.ecommerce_system.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Product {
    private UUID productId;
    private String name;
    private String description;
    private Double price;
    private int stockQuantity;
    private UUID categoryId;
    private Instant createdAt;
    private Instant updatedAt;
}
