package com.example.ecommerce_system.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequestDto {
    @NotNull(
            groups = AddCartItem.class,
            message = "productId is a required field"
    )
    private UUID productId;

    @Positive(
            groups = {UpdateCartItem.class, AddCartItem.class},
            message = "quantity must be positive"
    )
    @NotNull(
            groups = {UpdateCartItem.class, AddCartItem.class},
            message = "quantity is a required field"
    )
    private Integer quantity;
}
