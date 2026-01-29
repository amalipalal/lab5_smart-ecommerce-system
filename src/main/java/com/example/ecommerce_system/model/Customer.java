package com.example.ecommerce_system.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
@SuperBuilder
public class Customer {
    private UUID customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Instant createdAt;
    private boolean isActive;
}