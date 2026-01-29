package com.example.ecommerce_system.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
@SuperBuilder
public class User {
    private UUID userId;
    private String email;
    private String passwordHash;
    private Role role;
    private Instant createdAt;
}
