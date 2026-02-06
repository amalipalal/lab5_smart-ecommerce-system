package com.example.ecommerce_system.controller;

import com.example.ecommerce_system.dto.cart.AddCartItem;
import com.example.ecommerce_system.dto.cart.CartItemRequestDto;
import com.example.ecommerce_system.dto.cart.CartItemResponseDto;
import com.example.ecommerce_system.dto.cart.UpdateCartItem;
import com.example.ecommerce_system.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class CartGraphQLController {
    private final CartService cartService;

    @QueryMapping
    public List<CartItemResponseDto> getCustomerCartItems(@Argument String customerId) {
        UUID customerUuid = UUID.fromString(customerId);
        return cartService.getCartItemsByCustomer(customerUuid);
    }

    @MutationMapping
    public CartItemResponseDto addCartItem(
            @Argument String customerId,
            @Argument @Validated(AddCartItem.class) CartItemRequestDto request) {
        UUID customerUuid = UUID.fromString(customerId);
        return cartService.addToCart(customerUuid, request);
    }

    @MutationMapping
    public CartItemResponseDto updateCartItem(
            @Argument String customerId,
            @Argument String cartItemId,
            @Argument @Validated(UpdateCartItem.class) CartItemRequestDto request) {
        UUID customerUuid = UUID.fromString(customerId);
        UUID cartItemUuid = UUID.fromString(cartItemId);
        return cartService.updateCartItem(customerUuid, cartItemUuid, request);
    }

    @MutationMapping
    public Boolean removeFromCart(
            @Argument String customerId,
            @Argument String cartItemId) {
        UUID customerUuid = UUID.fromString(customerId);
        UUID cartItemUuid = UUID.fromString(cartItemId);
        cartService.removeFromCart(customerUuid, cartItemUuid);
        return true;
    }
}
