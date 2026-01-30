package com.example.ecommerce_system.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Data
@SuperBuilder
public class CustomerResponseDto {
    private UUID customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private boolean isActive;
    private Instant createdAt;
}
