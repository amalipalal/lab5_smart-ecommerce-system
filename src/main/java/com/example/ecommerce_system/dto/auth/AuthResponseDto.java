package com.example.ecommerce_system.dto.auth;

import com.example.ecommerce_system.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class AuthResponseDto {
    private UUID userId;
    private String email;
    private Role role;
    private Instant createdAt;
}
