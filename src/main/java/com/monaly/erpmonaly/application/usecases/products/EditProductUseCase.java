package com.monaly.erpmonaly.application.usecases.products;

import com.monaly.erpmonaly.application.DTOs.EditProductRequest;
import com.monaly.erpmonaly.application.gateways.products.ProductGateway;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.infra.persistence.ProductEntity;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

import static java.time.LocalTime.now;

@Service
public class EditProductUseCase {
    private final ProductGateway productGateway;

    public EditProductUseCase(ProductGateway productGateway) {
        this.productGateway = productGateway;
    }

    public Product edit(UserEntity userEntity, EditProductRequest data, UUID productId) throws Exception {
        UUID companyId = userEntity.getCompanyId();
        Product findProduct = productGateway.findByIdAndCompany(productId, companyId);

        if(!findProduct.getCompanyId().equals(companyId)){
            throw new IllegalAccessException("Você não pode editar um produto de outra empresa");
        }
        // Apagar busca do produto no Repository
        ProductEntity updateProduct = new ProductEntity(
                productId,
                companyId,
                data.name(),
                data.sku(),
                data.price(),
                data.description(),
                findProduct.getState(),
                findProduct.getCreatedAt(),
                OffsetDateTime.now(),
                findProduct.getDeleted_at()
        );

        Product product = productGateway.update(updateProduct, productId, companyId);

        return product;
    }
}
