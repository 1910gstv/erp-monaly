package com.monaly.erpmonaly.application.DTOs.stock;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateStockDTO(
        UUID companyId,
        UUID productId,
        int quantity,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime deletedAt
){}
