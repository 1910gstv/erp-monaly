package com.monaly.erpmonaly.application.gateways.products;

import com.monaly.erpmonaly.application.DTOs.products.CreateProductRequest;
import com.monaly.erpmonaly.domain.entities.products.Product;

import java.util.UUID;

public interface ProductGateway {
    Product save(CreateProductRequest request, UUID companyId);
}
