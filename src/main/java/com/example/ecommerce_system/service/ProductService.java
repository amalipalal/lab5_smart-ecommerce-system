package com.example.ecommerce_system.service;

import com.example.ecommerce_system.dto.product.ProductFilter;
import com.example.ecommerce_system.dto.product.ProductRequestDto;
import com.example.ecommerce_system.dto.product.ProductResponseDto;
import com.example.ecommerce_system.dto.product.ProductWithReviewsDto;
import com.example.ecommerce_system.dto.review.ReviewResponseDto;
import com.example.ecommerce_system.exception.category.CategoryNotFoundException;
import com.example.ecommerce_system.exception.product.ProductNotFoundException;
import com.example.ecommerce_system.model.Product;
import com.example.ecommerce_system.store.CategoryStore;
import com.example.ecommerce_system.store.ProductStore;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductStore productStore;
    private final CategoryStore categoryStore;
    private final ReviewService reviewService;
    private final CategoryService categoryService;

    /**
     * Create a new product.
     * Validates that the category exists before creating the product.
     */
    public ProductResponseDto createProduct(ProductRequestDto request) {
        checkThatCategoryExists(request.getCategoryId());

        Product product = new Product(
                UUID.randomUUID(),
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStock(),
                request.getCategoryId(),
                Instant.now(),
                Instant.now()
        );
        Product saved = this.productStore.createProduct(product);
        return map(saved);
    }

    private void checkThatCategoryExists(UUID categoryId) {
        categoryStore.getCategory(categoryId).orElseThrow(
                () -> new CategoryNotFoundException(categoryId.toString()));
    }

    private ProductResponseDto map(Product product) {
        return ProductResponseDto.builder()
                .productId(product.getProductId())
                .categoryId(product.getCategoryId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStockQuantity())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public ProductResponseDto getProduct(UUID productId) {
        Product product = this.productStore.getProduct(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));
        return map(product);
    }

    /**
     * Retrieve all products with pagination.
     */
    public List<ProductResponseDto> getAllProducts(int limit, int offset) {
        List<Product> products = this.productStore.getAllProducts(limit, offset);
        return products.stream().map(this::map).toList();
    }

    public int countProductsByFilter(ProductFilter filter) {
        return this.productStore.countProductsByFilter(filter);
    }

    /**
     * Delete a product by ID.
     * Validates that the product exists before deletion.
     */
    public void deleteProduct(UUID productId) {
        Product existing = this.productStore.getProduct(productId).orElseThrow(
                () -> new ProductNotFoundException(productId.toString()));
        this.productStore.deleteProduct(existing.getProductId());
    }

    /**
     * Search for products using a filter with pagination.
     */
    public List<ProductResponseDto> searchProducts(ProductFilter filter, int limit, int offset) {
        List<Product> products = this.productStore.searchProducts(filter, limit, offset);
        return products.stream().map(this::map).toList();
    }

    /**
     * Update an existing product.
     * Validates product existence and merges provided fields with existing values.
     */
    public ProductResponseDto updateProduct(UUID productId, ProductRequestDto request) {
        Product existing = this.productStore.getProduct(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        Product updated = new Product(
                existing.getProductId(),
                request.getName() != null ? request.getName() : existing.getName(),
                request.getDescription() != null ? request.getDescription() : existing.getDescription(),
                request.getPrice() != null ? request.getPrice() : existing.getPrice(),
                request.getStock() != null ? request.getStock() : existing.getStockQuantity(),
                request.getCategoryId() != null ? request.getCategoryId() : existing.getCategoryId(),
                existing.getCreatedAt(),
                Instant.now()
        );

        this.productStore.updateProduct(updated);
        return map(updated);
    }

    /**
     * Get all products with their categories and reviews.
     * Each product includes a limited number of reviews based on reviewLimit parameter.
     */
    public List<ProductWithReviewsDto> getAllProductsWithReviews(int limit, int offset, int reviewLimit) {
        List<Product> products = this.productStore.getAllProducts(limit, offset);
        return products.stream().map(product -> {
            List<ReviewResponseDto> reviews = reviewService.getReviewsByProduct(
                    product.getProductId(),
                    reviewLimit,
                    0
            );
            return mapToProductWithReviews(product, reviews);
        }).toList();
    }

    private ProductWithReviewsDto mapToProductWithReviews(Product product, List<ReviewResponseDto> reviews) {
        return ProductWithReviewsDto.builder()
                .productId(product.getProductId())
                .category(categoryService.getCategory(product.getCategoryId()))
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStockQuantity())
                .updatedAt(product.getUpdatedAt())
                .reviews(reviews)
                .build();
    }
}
