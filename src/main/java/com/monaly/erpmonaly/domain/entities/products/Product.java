package com.monaly.erpmonaly.domain.entities.products;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public final class Product {
    private UUID id;
    private UUID companyId;
    private String name;
    private String sku;
    private BigDecimal price;
    private String description;
    private String state;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;

    public Product(UUID id, UUID companyId, String name, String sku, BigDecimal price, String description, String state, OffsetDateTime createdAt, OffsetDateTime updatedAt, OffsetDateTime deletedAt) {
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

    public boolean deactivate() {
        if (state.equalsIgnoreCase("active")) {
            this.state = "inactive";
            this.updatedAt = OffsetDateTime.now();
            return true;
        }

        return false;
    }

    public boolean reactivate() {
        if (state.equalsIgnoreCase("inactive")) {
            this.state = "active";
            this.updatedAt = OffsetDateTime.now();
            return true;
        }

        return false;
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

    public String getSku() {
        return sku;
    }

    public void setState(String state) {
        this.state = state;
    }

    public UUID id() {
        return id;
    }

    public UUID companyId() {
        return companyId;
    }

    public String name() {
        return name;
    }

    public String sku() {
        return sku;
    }

    public BigDecimal price() {
        return price;
    }

    public String description() {
        return description;
    }

    public String state() {
        return state;
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }

    public OffsetDateTime updatedAt() {
        return updatedAt;
    }

    public OffsetDateTime deletedAt() {
        return deletedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Product) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.companyId, that.companyId) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.sku, that.sku) &&
                Objects.equals(this.price, that.price) &&
                Objects.equals(this.description, that.description) &&
                Objects.equals(this.state, that.state) &&
                Objects.equals(this.createdAt, that.createdAt) &&
                Objects.equals(this.updatedAt, that.updatedAt) &&
                Objects.equals(this.deletedAt, that.deletedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, companyId, name, sku, price, description, state, createdAt, updatedAt, deletedAt);
    }

    @Override
    public String toString() {
        return "Product[" +
                "id=" + id + ", " +
                "companyId=" + companyId + ", " +
                "name=" + name + ", " +
                "sku=" + sku + ", " +
                "price=" + price + ", " +
                "description=" + description + ", " +
                "state=" + state + ", " +
                "createdAt=" + createdAt + ", " +
                "updatedAt=" + updatedAt + ", " +
                "deletedAt=" + deletedAt + ']';
    }


}