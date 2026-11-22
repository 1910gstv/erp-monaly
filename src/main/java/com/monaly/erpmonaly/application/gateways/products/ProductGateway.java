package com.monaly.erpmonaly.application.gateways.products;

import com.monaly.erpmonaly.application.DTOs.EditProductRequest;
import com.monaly.erpmonaly.application.DTOs.products.CreateProductRequest;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.infra.persistence.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductGateway {
    Product save(CreateProductRequest request, UUID companyId);
    Page<Product> findAllByCompanyId(
            UUID companyId,
            String name,
            String sku,
            UUID id,
            String state,
            Pageable pageable
    );
    Product update(ProductEntity request, UUID productId, UUID companyId);
    Product findByIdAndCompany(UUID productId, UUID companyId);
}
