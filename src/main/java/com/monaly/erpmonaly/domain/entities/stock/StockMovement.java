package com.monaly.erpmonaly.domain.entities.stock;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class StockMovement {

    public UUID id;
    private UUID companyId;
    private final UUID productId;
    private final UUID userId;
    private String type;
    private int quantity;
    private String reason;
    private OffsetDateTime createdAt;

    public StockMovement(
            UUID id,
            UUID companyId,
            UUID productId, UUID userId, String type,
            int quantity, String reason,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.companyId = companyId;
        this.productId = productId;
        this.userId = userId;
        this.type = type;
        this.quantity = quantity;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public UUID getProductId() {
        return productId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getReason() {
        return reason;
    }
}
