package com.example.ecommerce_system.service;

import com.example.ecommerce_system.dto.customer.CustomerResponseDto;
import com.example.ecommerce_system.dto.review.ReviewRequestDto;
import com.example.ecommerce_system.dto.review.ReviewResponseDto;
import com.example.ecommerce_system.exception.customer.CustomerNotFoundException;
import com.example.ecommerce_system.exception.product.ProductNotFoundException;
import com.example.ecommerce_system.exception.review.CustomerHasNotOrderedProductException;
import com.example.ecommerce_system.model.Customer;
import com.example.ecommerce_system.model.Review;
import com.example.ecommerce_system.store.CustomerStore;
import com.example.ecommerce_system.store.OrdersStore;
import com.example.ecommerce_system.store.ProductStore;
import com.example.ecommerce_system.store.ReviewStore;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewStore reviewStore;
    private final ProductStore productStore;
    private final CustomerStore customerStore;
    private final OrdersStore ordersStore;

    /**
     * Create a new review for a product.
     * Validates that the product exists, the customer exists, and the customer has ordered and received (PROCESSED status) the product.
     */
    public ReviewResponseDto createReview(UUID productId, UUID customerId, ReviewRequestDto request) {
        productStore.getProduct(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        customerStore.getCustomer(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId.toString()));

        validateCustomerHasProcessedProduct(customerId, productId);

        Review review = Review.builder()
                .reviewId(UUID.randomUUID())
                .productId(productId)
                .customerId(customerId)
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(Instant.now())
                .build();

        Review savedReview = reviewStore.createReview(review);

        return mapToDto(savedReview);
    }

    private void validateCustomerHasProcessedProduct(UUID customerId, UUID productId) {
        boolean hasProcessedOrder = ordersStore.hasProcessedOrderWithProduct(customerId, productId);

        if (!hasProcessedOrder) {
            throw new CustomerHasNotOrderedProductException(
                    customerId.toString(),
                    productId.toString()
            );
        }
    }

    private ReviewResponseDto mapToDto(Review review) {
        Customer customer = customerStore.getCustomer(review.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(review.getCustomerId().toString()));

        var customerDto = mapToCustomerDto(customer);

        return ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .productId(review.getProductId())
                .customer(customerDto)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private CustomerResponseDto mapToCustomerDto(Customer customer) {
        return CustomerResponseDto.builder()
                .customerId(customer.getCustomerId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .createdAt(customer.getCreatedAt())
                .build();
    }

    /**
     * Retrieve paginated reviews for a specific product.
     * Validates product existence before fetching reviews. Each review includes customer details.
     */
    public List<ReviewResponseDto> getReviewsByProduct(UUID productId, int limit, int offset) {
        productStore.getProduct(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId.toString()));

        List<Review> reviews = reviewStore.getReviewsByProduct(productId, limit, offset);

        return reviews.stream()
                .map(this::mapToDto)
                .toList();
    }
}
