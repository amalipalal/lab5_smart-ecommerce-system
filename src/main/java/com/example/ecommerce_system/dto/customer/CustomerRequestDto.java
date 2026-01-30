package com.example.ecommerce_system.dto.customer;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomerRequestDto {

    @Pattern(
            regexp = "^\\+233[0-9]{9}$",
            message = "phone should be formatted as '+233xxxxxxxxx"
    )
    private String phone;

    private Boolean isActive;
}
