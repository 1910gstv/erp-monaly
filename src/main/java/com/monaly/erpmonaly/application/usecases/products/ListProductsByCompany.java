package com.monaly.erpmonaly.application.usecases.products;

import com.monaly.erpmonaly.application.gateways.products.ProductGateway;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListProductsByCompany {
    private final ProductGateway productGateway;

    public ListProductsByCompany(ProductGateway productGateway) {
        this.productGateway = productGateway;
    }

    public List<Product> listAllByCompany(UserEntity userEntity){
        UUID companyId = userEntity.getCompanyId();

        return productGateway.findAllByCompanyId(companyId);
    }

    // public Product listByIdAndCompany(){}

}
