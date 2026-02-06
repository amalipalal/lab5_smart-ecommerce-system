package com.example.ecommerce_system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter
public class Review {
    private UUID reviewId;
    private UUID productId;
    private UUID customerId;
    private Integer rating;
    private String comment;
    private Instant createdAt;
}
