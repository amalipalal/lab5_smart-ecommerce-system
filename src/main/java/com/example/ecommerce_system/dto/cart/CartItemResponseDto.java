package com.example.ecommerce_system.dto.cart;

import com.example.ecommerce_system.dto.product.ProductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDto {
    private UUID cartItemId;
    private UUID cartId;
    private ProductResponseDto product;
    private Integer quantity;
    private Instant addedAt;
}
