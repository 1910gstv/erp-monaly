package com.monaly.erpmonaly.domain.entities.stock;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class Stock {
    private UUID id;
    private UUID companyId;
    private final UUID productId;
    private int quantity;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;

    public Stock(UUID id, UUID companyId, UUID productId, int quantity, OffsetDateTime createdAt, OffsetDateTime updatedAt, OffsetDateTime deletedAt) {
        this.id = id;
        this.companyId = companyId;
        this.productId = productId;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public UUID getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }
}
