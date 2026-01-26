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
public class Category {
    private UUID categoryId;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
