package com.monaly.erpmonaly.application.usecases.products;

import com.monaly.erpmonaly.application.DTOs.products.CreateProductRequest;
import com.monaly.erpmonaly.application.gateways.products.ProductGateway;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.domain.exceptions.DuplicationException;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateProductUseCase {
    private final ProductGateway productGateway;

    public CreateProductUseCase(ProductGateway productGateway) {
        this.productGateway = productGateway;
    }

    public Product create(CreateProductRequest request, UserEntity userEntity){
        UUID companyId = userEntity.getCompanyId();
        Product createdProduct = null;
        try {
            createdProduct = productGateway.save(request, companyId);
        } catch(DataIntegrityViolationException e){
            String message = e.getMostSpecificCause().getMessage();
            if (message != null && (message.contains("uq_products_company_sku"))) {
                throw new DuplicationException("Já existe produtos com essa SKU cadastrado nessa empresa");
            }
            throw e;
        }

        return createdProduct;
    }

}
