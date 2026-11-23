package com.monaly.erpmonaly.application.gateways.products;

import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.infra.persistence.ProductEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class ProductEntityMapper {
    public ProductEntity toEntity(Product productDomainObj){
        return new ProductEntity(
            productDomainObj.id(),
            productDomainObj.companyId(),
            productDomainObj.name(),
            productDomainObj.sku(),
            productDomainObj.price(),
            productDomainObj.description(),
            productDomainObj.state(),
            productDomainObj.createdAt(),
            productDomainObj.updatedAt(),
            productDomainObj.deletedAt()
        );
    };

    public Product toDomain(ProductEntity entity){
        return new Product(
                entity.getId(),
                entity.getCompanyId(),
                entity.getName(),
                entity.getSku(),
                entity.getPrice(),
                entity.getDescription(),
                entity.getState(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    };
}
