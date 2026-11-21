package com.monaly.erpmonaly.application.DTOs;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record EditProductRequest(
        @NotBlank
        @Size(max = 150)
        String name,
        @NotNull
        @DecimalMin(value = "0.00")
        @Digits(integer = 8, fraction = 2)
        BigDecimal price,
        @Size(max = 100)
        @Pattern(regexp = ".*\\S.*", message = "SKU não pode conter apenas espaços")
        String sku,
        @NotBlank
        @Size(max = 400)
        String description
) {
}


