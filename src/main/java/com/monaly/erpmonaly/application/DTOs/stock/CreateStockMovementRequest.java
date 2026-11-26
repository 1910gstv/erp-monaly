package com.monaly.erpmonaly.application.DTOs.stock;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record CreateStockMovementRequest(
        @NotBlank
        @Size(max = 150)
        String type,
        @NotNull
        int quantity,
        @NotBlank
        String reason,
        @NotNull
        UUID productId
){}
