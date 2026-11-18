package com.monaly.erpmonaly.application.usecases.products;

import com.monaly.erpmonaly.application.gateways.products.ProductGateway;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListProductsByCompany {
    private final ProductGateway productGateway;

    public ListProductsByCompany(ProductGateway productGateway) {
        this.productGateway = productGateway;
    }

    public Page<Product> listAllByCompany(
            UserEntity userEntity,
            int page,
            int size,
            String name,
            String sku,
            UUID id,
            String state
    ){
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("id")
                )
        );
        UUID companyId = userEntity.getCompanyId();

        return productGateway.findAllByCompanyId(companyId, name, sku, id, state, pageable);
    }

    // public Product listByIdAndCompany(){}

}
