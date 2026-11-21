package com.monaly.erpmonaly.infra.gateways.products;

import com.monaly.erpmonaly.application.DTOs.EditProductRequest;
import com.monaly.erpmonaly.application.DTOs.products.CreateProductRequest;
import com.monaly.erpmonaly.application.gateways.products.ProductGateway;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.domain.entities.users.User;
import com.monaly.erpmonaly.infra.persistence.ProductEntity;
import com.monaly.erpmonaly.infra.persistence.ProductRepository;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class ProductRepositoryGateway implements ProductGateway {

    private final ProductRepository productRepository;

    public ProductRepositoryGateway(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product save(CreateProductRequest request, UUID companyId) {
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        ProductEntity product = new ProductEntity(
                id,
                companyId,
                request.name(),
                request.price(),
                request.sku(),
                request.description(),
                request.state(),
                now,
                now,
                null

        );
        ProductEntity productSaved = productRepository.save(product);

        return new Product(
                productSaved.getId(),
                productSaved.getCompanyId(),
                productSaved.getName(),
                productSaved.getSku(),
                productSaved.getPrice(),
                productSaved.getDescription(),
                productSaved.getState(),
                productSaved.getCreatedAt(),
                productSaved.getUpdatedAt(),
                productSaved.getDeletedAt()
        );
    }

    @Override
    public Page<Product> findAllByCompanyId(UUID companyId, String name, String sku, UUID id, String state, Pageable pageable) {
        Page<ProductEntity> products = productRepository.findAllProductsByCompanyId(companyId, name, sku, id, state, pageable);
        return products.map(productEntity -> new Product(
                productEntity.getId(),
                productEntity.getCompanyId(),
                productEntity.getName(),
                productEntity.getSku(),
                productEntity.getPrice(),
                productEntity.getDescription(),
                productEntity.getState(),
                productEntity.getCreatedAt(),
                productEntity.getUpdatedAt(),
                productEntity.getDeletedAt()
        ));
    }

    @Override
    public Product update(EditProductRequest request, UUID companyId) {
        return null;
    }

}
