package com.example.ecommerce_system.controller;

import com.example.ecommerce_system.dto.ProductFilter;
import com.example.ecommerce_system.dto.SuccessResponseDto;
import com.example.ecommerce_system.dto.product.CreateProductRequest;
import com.example.ecommerce_system.dto.product.ProductRequestDto;
import com.example.ecommerce_system.dto.product.ProductResponseDto;
import com.example.ecommerce_system.dto.product.UpdateProductRequest;
import com.example.ecommerce_system.service.ProductService;
import com.example.ecommerce_system.util.SuccessResponseHandler;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public SuccessResponseDto<List<ProductResponseDto>> getAllProducts(
            @RequestParam @Min(1) int limit,
            @RequestParam @Min(0) int offset
    ) {
        List<ProductResponseDto> products = productService.getAllProducts(limit, offset);
        return SuccessResponseHandler.generateSuccessResponse(HttpStatus.OK, products);
    }

    @GetMapping("/{id}")
    public SuccessResponseDto<ProductResponseDto> getProductId(@PathVariable UUID id) {
        var product = productService.getProduct(id);
        return SuccessResponseHandler.generateSuccessResponse(HttpStatus.OK, product);
    }

    @GetMapping("/search")
    public SuccessResponseDto<List<ProductResponseDto>> searchProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) UUID category,
            @RequestParam @Min(1) int limit,
            @RequestParam @Min(0) int offset
    ) {
        ProductFilter filter = new ProductFilter(query, category);
        List<ProductResponseDto> products = productService.searchProducts(filter, limit, offset);
        return SuccessResponseHandler.generateSuccessResponse(HttpStatus.OK, products);
    }

    @PostMapping
    public SuccessResponseDto<ProductResponseDto> addProduct(
            @RequestBody @Validated(CreateProductRequest.class) ProductRequestDto product
    ) {
        var productCreated = productService.createProduct(product);
        return SuccessResponseHandler.generateSuccessResponse(HttpStatus.CREATED, productCreated);
    }

    @PatchMapping("/{id}")
    public SuccessResponseDto<ProductResponseDto> updateProduct(
            @PathVariable UUID id,
            @RequestBody @Validated(UpdateProductRequest.class) ProductRequestDto update
    ) {
        var productCreated = productService.updateProduct(id, update);
        return SuccessResponseHandler.generateSuccessResponse(HttpStatus.OK, productCreated);
    }

    @DeleteMapping("/{id}")
    public SuccessResponseDto<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return SuccessResponseHandler.generateSuccessResponse(HttpStatus.NO_CONTENT, null);
    }

}
