package com.monaly.erpmonaly.domain.entities.products;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

public record Product(
    UUID id,
    UUID companyId,
    String name,
    String sku,
    BigDecimal price,
    String description,
    String state,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    OffsetDateTime deletedAt
){
    public Product(UUID id, UUID companyId, String name, String sku, BigDecimal price, String description, String state, OffsetDateTime createdAt, OffsetDateTime updatedAt, OffsetDateTime deletedAt){
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.description = description;
        this.state = state;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getState() {
        return state;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public OffsetDateTime getDeleted_at() {
        return deletedAt;
    }

    public String getSku(){
        return sku;
    }

}