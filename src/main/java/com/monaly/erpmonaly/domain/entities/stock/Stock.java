package com.monaly.erpmonaly.domain.entities.stock;

import java.time.OffsetDateTime;
import java.util.Objects;
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

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }

    public UUID getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(OffsetDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void calculateNewStock(String type, int receivedQuantity) throws Exception {
        if(Objects.equals(type, "entry")){
            this.quantity += receivedQuantity;
        } else if (Objects.equals(type, "exit")) {
            if(receivedQuantity < this.quantity){
                throw new Exception("Não é possível retirar mais produto do que tem no estoque");
            }
            this.quantity -= receivedQuantity;
        } else if (Objects.equals(type, "adjustment")){
            this.quantity = receivedQuantity;
        }

        this.updatedAt = OffsetDateTime.now();

    };
}
