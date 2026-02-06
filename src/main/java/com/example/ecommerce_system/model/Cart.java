package com.example.ecommerce_system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter
public class Cart {
    private final UUID cartId;
    private final UUID customerId;
    private final Instant createdAt;
    private final Instant updatedAt;
}
