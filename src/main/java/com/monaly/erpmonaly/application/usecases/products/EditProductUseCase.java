package com.monaly.erpmonaly.application.usecases.products;

import com.monaly.erpmonaly.application.DTOs.EditProductRequest;
import com.monaly.erpmonaly.application.DTOs.products.CreateProductRequest;
import com.monaly.erpmonaly.application.gateways.products.ProductGateway;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.time.LocalTime.now;

@Service
public class EditProductUseCase {
    private final ProductGateway productGateway;

    public EditProductUseCase(ProductGateway productGateway) {
        this.productGateway = productGateway;
    }

    public Product edit(UserEntity userEntity, EditProductRequest data, UUID productId){
        UUID companyId = userEntity.getCompanyId();

        Product product = productGateway.save(data, companyId);

        return product;
    }
}
