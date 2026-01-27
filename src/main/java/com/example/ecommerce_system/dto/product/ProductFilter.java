package com.example.ecommerce_system.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class ProductFilter {
    private String name;
    private UUID categoryId;

    public boolean hasName() {
        return this.name != null;
    }

    public boolean hasCategoryId() {
        return this.categoryId != null;
    }
}
