package com.monaly.erpmonaly.infra.gateways.products;

import com.monaly.erpmonaly.application.DTOs.products.CreateProductRequest;
import com.monaly.erpmonaly.application.gateways.products.ProductGateway;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.domain.entities.users.User;
import com.monaly.erpmonaly.infra.persistence.ProductEntity;
import com.monaly.erpmonaly.infra.persistence.ProductRepository;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
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
    public List<Product> findAllByCompanyId(UUID companyId) {
        List<ProductEntity> products = productRepository.findAllProductsByCompanyId(companyId);
        return products.stream().map(productEntity -> new Product(
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
        )).toList();
    }

}
