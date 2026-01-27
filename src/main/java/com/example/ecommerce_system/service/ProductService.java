package com.example.ecommerce_system.service;

import com.example.ecommerce_system.dto.product.ProductFilter;
import com.example.ecommerce_system.dto.product.ProductRequestDto;
import com.example.ecommerce_system.dto.product.ProductResponseDto;
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

    /**
     * Create a new product and persist it via {@link com.example.ecommerce_system.store.ProductStore#createProduct(com.example.ecommerce_system.model.Product)}.
     *
     * @param request the incoming {@link com.example.ecommerce_system.dto.product.ProductRequestDto}
     * @return a {@link com.example.ecommerce_system.dto.product.ProductResponseDto} containing identifiers and timestamps for the new product
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

    /**
     * Retrieve a product by id.
     *
     * Uses {@link com.example.ecommerce_system.store.ProductStore#getProduct(java.util.UUID)} and throws {@link com.example.ecommerce_system.exception.product.ProductNotFoundException}
     * when no product is found.
     *
     * @param productId the product identifier
     * @return a {@link com.example.ecommerce_system.dto.product.ProductResponseDto} for the found product
     * @throws com.example.ecommerce_system.exception.product.ProductNotFoundException if the product does not exist
     */
    public ProductResponseDto getProduct(UUID productId) {
        Product product = this.productStore.getProduct(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));
        return map(product);
    }

    public List<ProductResponseDto> getAllProducts(int limit, int offset) {
        List<Product> products = this.productStore.getAllProducts(limit, offset);
        return products.stream().map(this::map).toList();
    }

    /**
     * Count products matching a filter.
     *
     * @param filter the {@link ProductFilter} criteria
     * @return number of products matching the filter
     */
    public int countProductsByFilter(ProductFilter filter) {
        return this.productStore.countProductsByFilter(filter);
    }

    /**
     * Delete a product by id.
     *
     * Ensures the product exists by calling {@link com.example.ecommerce_system.store.ProductStore#getProduct(java.util.UUID)} before delegating
     * to {@link com.example.ecommerce_system.store.ProductStore#deleteProduct(java.util.UUID)}.
     *
     * @param productId the product identifier to delete
     * @throws com.example.ecommerce_system.exception.product.ProductNotFoundException if the product does not exist
     */
    public void deleteProduct(UUID productId) {
        Product existing = this.productStore.getProduct(productId).orElseThrow(
                () -> new ProductNotFoundException(productId.toString()));
        this.productStore.deleteProduct(existing.getProductId());
    }

    /**
     * Search for products using a filter with paging.
     *
     * Delegates to {@link com.example.ecommerce_system.store.ProductStore#searchProducts(ProductFilter, int, int)}.
     *
     * @param filter the {@link ProductFilter} to apply
     * @param limit  maximum number of results
     * @param offset zero-based offset for paging
     * @return list of {@link com.example.ecommerce_system.dto.product.ProductResponseDto}
     */
    public List<ProductResponseDto> searchProducts(ProductFilter filter, int limit, int offset) {
        List<Product> products = this.productStore.searchProducts(filter, limit, offset);
        return products.stream().map(this::map).toList();
    }

    /**
     * Update an existing product.
     *
     * Validates presence of the product via {@link com.example.ecommerce_system.store.ProductStore#getProduct(java.util.UUID)} and
     * delegates persistence to {@link com.example.ecommerce_system.store.ProductStore#updateProduct(com.example.ecommerce_system.model.Product)}.
     *
     * @param productId the product id to update
     * @param request   the incoming {@link com.example.ecommerce_system.dto.product.ProductRequestDto} with optional fields
     * @throws com.example.ecommerce_system.exception.product.ProductNotFoundException if the target product does not exist
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
}
