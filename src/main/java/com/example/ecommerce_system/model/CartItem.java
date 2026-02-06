package com.example.ecommerce_system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter
public class CartItem {
    private final UUID cartItemId;
    private final UUID cartId;
    private final UUID productId;
    private final int quantity;
    private final Instant addedAt;
}
