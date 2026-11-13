package com.monaly.erpmonaly.domain.entities.products;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record Product(
    UUID id,
    UUID companyId,
    String name,
    BigDecimal price,
    String description,
    String state,
    Date createdAt,
    Date updatedAt,
    Date deleted_at
){
    public Product(UUID id, UUID companyId, String name, BigDecimal price, String description, String state, Date createdAt, Date updatedAt, Date deleted_at){
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.state = state;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deleted_at = deleted_at;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Date getDeleted_at() {
        return deleted_at;
    }

}