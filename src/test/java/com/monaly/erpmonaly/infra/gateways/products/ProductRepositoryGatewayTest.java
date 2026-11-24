package com.monaly.erpmonaly.infra.gateways.products;

import com.monaly.erpmonaly.application.DTOs.products.CreateProductRequest;
import com.monaly.erpmonaly.application.gateways.products.ProductEntityMapper;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.infra.persistence.ProductEntity;
import com.monaly.erpmonaly.infra.persistence.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductRepositoryGatewayTest {
    private ProductRepository productRepository;
    private ProductRepositoryGateway gateway;
    private  ProductEntityMapper productEntityMapper;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        productEntityMapper = mock(ProductEntityMapper.class);
        gateway = new ProductRepositoryGateway(productRepository, productEntityMapper);
    }

    @Test
    void persistsProductFieldsAndMapsSavedEntityToDomain() {
        UUID companyId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        CreateProductRequest request = new CreateProductRequest(
                "Product", new BigDecimal("19.99"), "SKU-1", "Description", "ACTIVE"
        );
        when(productRepository.save(any(ProductEntity.class)))
                .thenAnswer(invocation -> {
                    ProductEntity entity = invocation.getArgument(0);
                    entity.setCreatedAt(now);
                    entity.setUpdatedAt(now);
                    return entity;
                });

        Product saved = gateway.create(request, companyId);

        ArgumentCaptor<ProductEntity> entityCaptor = ArgumentCaptor.forClass(ProductEntity.class);
        verify(productRepository).save(entityCaptor.capture());
        ProductEntity persisted = entityCaptor.getValue();
        assertEquals(companyId, persisted.getCompanyId());
        assertEquals(request.name(), persisted.getName());
        assertEquals(request.sku(), persisted.getSku());
        assertEquals(request.price(), persisted.getPrice());
        assertEquals(request.description(), persisted.getDescription());
        assertEquals(request.state(), persisted.getState());
        assertNull(persisted.getDeletedAt());
        assertEquals(persisted.getId(), saved.id());
        assertEquals(companyId, saved.companyId());
        assertEquals(request.sku(), saved.sku());
        assertEquals(request.price(), saved.price());
    }

    @Test
    void persistsNullSkuWhenSkuIsOmitted() {
        CreateProductRequest request = new CreateProductRequest(
                "Product", BigDecimal.ONE, null, "Description", "ACTIVE"
        );
        when(productRepository.save(any(ProductEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        gateway.create(request, UUID.randomUUID());

        ArgumentCaptor<ProductEntity> entityCaptor = ArgumentCaptor.forClass(ProductEntity.class);
        verify(productRepository).save(entityCaptor.capture());
        assertNull(entityCaptor.getValue().getSku());
    }
}
