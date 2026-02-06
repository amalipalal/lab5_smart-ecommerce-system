package com.example.ecommerce_system.service;

import com.example.ecommerce_system.dto.cart.CartItemRequestDto;
import com.example.ecommerce_system.dto.cart.CartItemResponseDto;
import com.example.ecommerce_system.exception.cart.CartItemNotFoundException;
import com.example.ecommerce_system.exception.cart.CartItemAuthorizationException;
import com.example.ecommerce_system.exception.customer.CustomerNotFoundException;
import com.example.ecommerce_system.exception.product.ProductNotFoundException;
import com.example.ecommerce_system.model.Cart;
import com.example.ecommerce_system.model.CartItem;
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
     * Add a product to a customer's cart. If the customer does not have a cart yet, one is created.
     * Validates that both customer and product exist before adding the item.
     */
    public CartItemResponseDto addToCart(UUID customerId, CartItemRequestDto request) {
        checkThatCustomerExists(customerId);

        checkThatProductExists(request.getProductId());

        Cart cart = getOrCreateCartForCustomer(customerId);

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
     * Checks that the cart item exists and belongs to the customer.
     */
    public void removeFromCart(UUID customerId, UUID cartItemId) {
        checkThatCustomerExists(customerId);

        CartItem cartItem = cartStore.getCartItem(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId.toString()));

        checkCartItemAuthorization(customerId, cartItem);

        cartStore.removeCartItem(cartItemId);
    }

    private void checkThatCustomerExists(UUID customerId) {
        customerStore.getCustomer(customerId).orElseThrow(
                () -> new CustomerNotFoundException(customerId.toString())
        );
    }

    /**
     * Update the quantity of a cart item in the customer's cart.
     * Checks that the cart item exists and belongs to the customer.
     *
     * @return the updated cart item with full product details
     */
    public CartItemResponseDto updateCartItem(UUID customerId, UUID cartItemId, CartItemRequestDto request) {
        checkThatCustomerExists(customerId);

        CartItem cartItem = cartStore.getCartItem(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId.toString()));

        checkCartItemAuthorization(customerId, cartItem);

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
     * Get all cart items for a customer.</p>
     * Delegates to {@link com.example.ecommerce_system.store.CartStore#getCartByCustomerId(java.util.UUID)}
     * and {@link com.example.ecommerce_system.store.CartStore#getCartItems(java.util.UUID)}.
     */
    public List<CartItemResponseDto> getCartItemsByCustomer(UUID customerId) {
        checkThatCustomerExists(customerId);

        Optional<Cart> cartOpt = this.cartStore.getCartByCustomerId(customerId);

        if (cartOpt.isEmpty()) return List.of();

        List<CartItem> cartItems = this.cartStore.getCartItems(cartOpt.get().getCartId());

        return cartItems.stream()
                .map(this::mapToDto)
                .toList();
    }
}
