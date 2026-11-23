package com.monaly.erpmonaly.infra.gateways.products;

import com.monaly.erpmonaly.application.DTOs.EditProductRequest;
import com.monaly.erpmonaly.application.DTOs.products.CreateProductRequest;
import com.monaly.erpmonaly.application.gateways.products.ProductEntityMapper;
import com.monaly.erpmonaly.application.gateways.products.ProductGateway;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.domain.entities.users.User;
import com.monaly.erpmonaly.infra.persistence.ProductEntity;
import com.monaly.erpmonaly.infra.persistence.ProductRepository;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import jakarta.persistence.EntityNotFoundException;
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
    private final ProductEntityMapper productEntityMapper;

    public ProductRepositoryGateway(ProductRepository productRepository, ProductEntityMapper productEntityMapper) {
        this.productRepository = productRepository;
        this.productEntityMapper = productEntityMapper;
    }

    @Override
    public Product save(CreateProductRequest request, UUID companyId) {
        UUID id = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        ProductEntity product = new ProductEntity(
                id,
                companyId,
                request.name(),
                request.sku(),
                request.price(),
                request.description(),
                request.state(),
                now,
                now,
                null
        );
        ProductEntity productSaved = productRepository.save(product);

        return productEntityMapper.toDomain(productSaved);
    }

    @Override
    public Page<Product> findAllByCompanyId(UUID companyId, String name, String sku, UUID id, String state, Pageable pageable) {
        Page<ProductEntity> products = productRepository.findAllProductsByCompanyId(companyId, name, sku, id, state, pageable);
        return products.map(productEntityMapper::toDomain);
    }

    @Override
    public Product update(ProductEntity request, UUID productId, UUID companyId) {

        ProductEntity saved = productRepository.save(request);

        return productEntityMapper.toDomain(saved);
    }

    public Product findByIdAndCompany(UUID productId, UUID companyId){
        ProductEntity entity = productRepository.findByIdAndCompanyId(productId, companyId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        return productEntityMapper.toDomain(entity);
    }

}
