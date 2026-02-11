package com.example.ecommerce_system.dto.auth;

import com.example.ecommerce_system.model.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponseDto {
    private UUID userId;
    private String email;
    private Role role;
    private Instant createdAt;
    private String token;
}
