package com.monaly.erpmonaly.application.gateways.products;

import com.monaly.erpmonaly.application.DTOs.products.CreateProductRequest;
import com.monaly.erpmonaly.domain.entities.products.Product;

import java.util.List;
import java.util.UUID;

public interface ProductGateway {
    Product save(CreateProductRequest request, UUID companyId);
    List<Product> findAllByCompanyId(UUID companyId);
}
