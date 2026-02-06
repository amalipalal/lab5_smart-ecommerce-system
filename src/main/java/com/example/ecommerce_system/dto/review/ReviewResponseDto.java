package com.example.ecommerce_system.dto.review;

import com.example.ecommerce_system.dto.customer.CustomerResponseDto;
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
public class ReviewResponseDto {
    private UUID reviewId;
    private UUID productId;
    private CustomerResponseDto customer;
    private Integer rating;
    private String comment;
    private Instant createdAt;
}
