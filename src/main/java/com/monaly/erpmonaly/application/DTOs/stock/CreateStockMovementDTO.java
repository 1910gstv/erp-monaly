package com.monaly.erpmonaly.application.DTOs.stock;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateStockMovementDTO(
        UUID companyId,
        UUID productId,
        UUID userId,
        String type,
        int quantity,
        String reason,
        OffsetDateTime createdAt
){}
