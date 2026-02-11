package com.example.ecommerce_system.service;

import com.example.ecommerce_system.dto.cart.CartItemRequestDto;
import com.example.ecommerce_system.dto.cart.CartItemResponseDto;
import com.example.ecommerce_system.exception.cart.CartItemNotFoundException;
import com.example.ecommerce_system.exception.cart.CartItemAuthorizationException;
import com.example.ecommerce_system.exception.customer.CustomerNotFoundException;
import com.example.ecommerce_system.exception.product.ProductNotFoundException;
import com.example.ecommerce_system.model.Cart;
import com.example.ecommerce_system.model.CartItem;
import com.example.ecommerce_system.model.Customer;
import com.example.ecommerce_system.store.CartStore;
import com.example.ecommerce_system.store.CustomerStore;
import com.example.ecommerce_system.store.ProductStore;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartService {

    private final CartStore cartStore;
    private final CustomerStore customerStore;
    private final ProductStore productStore;
    private final ProductService productService;

    /**
     * Add a product to a customer's cart.
     * Creates a cart if the customer doesn't have one yet. Validates customer and product existence.
     */
    public CartItemResponseDto addToCart(UUID userId, CartItemRequestDto request) {
        var customer = checkThatCustomerExists(userId);

        checkThatProductExists(request.getProductId());

        Cart cart = getOrCreateCartForCustomer(customer.getCustomerId());

        CartItem cartItem = CartItem.builder()
                .cartItemId(UUID.randomUUID())
                .cartId(cart.getCartId())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .addedAt(Instant.now())
                .build();

        this.cartStore.addCartItem(cartItem);
        return mapToDto(cartItem);
    }

    private void checkThatProductExists(UUID productId) {
        productStore.getProduct(productId).orElseThrow(
                () -> new ProductNotFoundException(productId.toString())
        );
    }

    private Cart getOrCreateCartForCustomer(UUID customerId) {
        Optional<Cart> existingCart = this.cartStore.getCartByCustomerId(customerId);

        if (existingCart.isPresent()) return existingCart.get();

        Cart newCart = Cart.builder()
                .cartId(UUID.randomUUID())
                .customerId(customerId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return this.cartStore.createCart(newCart);
    }

    private CartItemResponseDto mapToDto(CartItem cartItem) {
        var product = this.productService.getProduct(cartItem.getProductId());

        return CartItemResponseDto.builder()
                .cartItemId(cartItem.getCartItemId())
                .cartId(cartItem.getCartId())
                .product(product)
                .quantity(cartItem.getQuantity())
                .addedAt(cartItem.getAddedAt())
                .build();
    }

    /**
     * Remove a cart item from the customer's cart.
     * Validates that the cart item exists and belongs to the customer before removal.
     */
    public void removeFromCart(UUID userId, UUID cartItemId) {
        var customer = checkThatCustomerExists(userId);
        var customerId = customer.getCustomerId();

        CartItem cartItem = cartStore.getCartItem(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId.toString()));

        checkCartItemAuthorization(customerId, cartItem);

        cartStore.removeCartItem(cartItemId);
    }

    private Customer checkThatCustomerExists(UUID userId) {
        return customerStore.getCustomerByUserId(userId).orElseThrow(
                () -> new CustomerNotFoundException(userId.toString())
        );
    }

    /**
     * Update the quantity of a cart item in the customer's cart.
     * Validates that the cart item exists and belongs to the customer. Returns the updated cart item with full product details.
     */
    public CartItemResponseDto updateCartItem(UUID userId, UUID cartItemId, CartItemRequestDto request) {
        var customer = checkThatCustomerExists(userId);

        CartItem cartItem = cartStore.getCartItem(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId.toString()));

        checkCartItemAuthorization(customer.getCustomerId(), cartItem);

        cartStore.updateCartItem(cartItemId, request.getQuantity());
        return getUpdatedCartItem(cartItemId);
    }

    private void checkCartItemAuthorization(UUID customerId, CartItem cartItem) {
        Optional<Cart> cartOpt = cartStore.getCartByCustomerId(customerId);
        if (cartOpt.isEmpty() || !cartItem.getCartId().equals(cartOpt.get().getCartId())) {
            throw new CartItemAuthorizationException(cartItem.getCartItemId().toString());
        }
    }

    private CartItemResponseDto getUpdatedCartItem(UUID cartItemId) {
        CartItem cartItem = this.cartStore.getCartItem(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found after update: " + cartItemId));

        return mapToDto(cartItem);
    }

    /**
     * Get all cart items for a customer.
     * Returns an empty list if the customer has no cart. Each cart item includes full product details.
     */
    public List<CartItemResponseDto> getCartItemsByCustomer(UUID userId) {
        var customer = checkThatCustomerExists(userId);

        Optional<Cart> cartOpt = this.cartStore.getCartByCustomerId(customer.getCustomerId());

        if (cartOpt.isEmpty()) return List.of();

        List<CartItem> cartItems = this.cartStore.getCartItems(cartOpt.get().getCartId());

        return cartItems.stream()
                .map(this::mapToDto)
                .toList();
    }
}
