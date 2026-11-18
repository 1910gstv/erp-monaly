package com.monaly.erpmonaly.api.controllers.products;

import com.monaly.erpmonaly.application.DTOs.products.CreateProductRequest;
import com.monaly.erpmonaly.application.usecases.products.CreateProductUseCase;
import com.monaly.erpmonaly.application.usecases.products.ListProductsByCompany;
import com.monaly.erpmonaly.domain.entities.products.Product;
import com.monaly.erpmonaly.infra.persistence.UserEntity;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("products")
public class ProductsController {
    private final CreateProductUseCase createProductUseCase;
    private final ListProductsByCompany listProductsByCompany;

    public ProductsController(CreateProductUseCase createProductUseCase, ListProductsByCompany listProductsByCompany) {
        this.createProductUseCase = createProductUseCase;
        this.listProductsByCompany = listProductsByCompany;
    }
    @PostMapping("/create")
    public Product create(@Valid @RequestBody CreateProductRequest request, @AuthenticationPrincipal UserEntity userEntity) throws Exception{
        return createProductUseCase.create(request, userEntity);
    }

    @GetMapping("/all")
    public Page<Product> findAll(@AuthenticationPrincipal UserEntity userEntity,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String name,
                                 @RequestParam(required = false) String sku,
                                 @RequestParam(required = false) UUID id,
                                 @RequestParam(required = false) String state
    ) throws Exception {

        return listProductsByCompany.listAllByCompany(userEntity, page, size, name, sku, id, state);
    }
}
