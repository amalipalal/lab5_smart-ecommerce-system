package com.example.ecommerce_system.controller.graphql;

import com.example.ecommerce_system.dto.product.ProductWithReviewsDto;
import com.example.ecommerce_system.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@AllArgsConstructor
public class ProductGraphQlController {

    private final ProductService productService;

    /**
     * Retrieves all products with their reviews.
     */
    @QueryMapping
    public List<ProductWithReviewsDto> getAllProductsWithReviews(
            @Argument int limit,
            @Argument int offset,
            @Argument(name = "reviewLimit") int reviewLimit
    ) {
        return productService.getAllProductsWithReviews(limit, offset, reviewLimit);
    }
}
