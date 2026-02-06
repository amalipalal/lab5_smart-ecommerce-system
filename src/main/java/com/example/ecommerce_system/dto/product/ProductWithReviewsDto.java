package com.example.ecommerce_system.dto.product;

import com.example.ecommerce_system.dto.category.CategoryResponseDto;
import com.example.ecommerce_system.dto.review.ReviewResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ProductWithReviewsDto {
    private UUID productId;
    private CategoryResponseDto category;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Instant updatedAt;
    private List<ReviewResponseDto> reviews;
}
