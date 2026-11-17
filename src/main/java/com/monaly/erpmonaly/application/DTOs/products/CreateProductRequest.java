package com.monaly.erpmonaly.application.DTOs.products;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateProductRequest(
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
        String description,
        @NotBlank
        String state
) {};
